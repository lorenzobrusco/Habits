package com.unical.informatica.lorenzo.habits;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class WatchFace extends CanvasWatchFaceService implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, SensorEventListener {

    /**
     * Strings used to comunicate with the paired wearable
     */
    private static final String WEAR_MESSAGE_PATH = "/message";

    /**
     * Google connection
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * sensormanager
     */
    private SensorManager mSensorManager;

    /**
     * Sensor to access at the heart rate
     */
    private Sensor mHeartRateSensor;
    private int currentHeartRate = 0;

    /**
     * Sensor to detection shake
     */
    private ShakeDetector mShakeDetector;
    private Sensor mAcceleration;

    /**
     * Type Watch face
     */
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SERIF, Typeface.NORMAL);
    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    /**
     * provide watch face implementations
     */
    @Override
    public Engine onCreateEngine() {
        this.mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.setupGoogleApiClient();
        this.setupHeartRateSensor();
        this.setupVibrate();
        return new Engine();
    }

    @Override
    public void onDestroy() {
        this.mSensorManager.unregisterListener(mShakeDetector);
        this.mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(this.mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
            Log.d("wear","request heart rate");
            this.sendMessage(WEAR_MESSAGE_PATH, String.valueOf(this.currentHeartRate));
        }
    }

    private void sendMessage(final String path, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(WatchFace.this.mGoogleApiClient).await();
                for (Node node : nodesResult.getNodes()) {
                    MessageApi.SendMessageResult sendMessageResult = Wearable.MessageApi.sendMessage(WatchFace.this.mGoogleApiClient,
                            node.getId(), path, message.getBytes()).await();
                }
            }
        }).start();
    }

    private void setupGoogleApiClient() {
        /** Setup googleApiClient and it try to connection */
        Log.d("connection", "wearable is connect");
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        this.mGoogleApiClient.connect();

    }

    private void setupHeartRateSensor() {
        this.mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        this.mSensorManager.registerListener(this, this.mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setupVibrate() {
        this.mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                Vibrator vibrator = (Vibrator) getSystemService(WatchFace.this.VIBRATOR_SERVICE);
                MakePatternToVibrate toVibrate = new MakePatternToVibrate(500, 100, 500, 1000);
                vibrator.vibrate(toVibrate.setupPattern(), -1);
            }
        });
        this.mSensorManager.registerListener(this.mShakeDetector, this.mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE && sensorEvent.values.length > 0) {
            int newValue = Math.round(sensorEvent.values[0]);
            if (currentHeartRate != newValue && newValue != 0) {
                this.currentHeartRate = newValue;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private static class EngineHandler extends Handler {
        private final WeakReference<Engine> mWeakReference;

        public EngineHandler(WatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    /**
     * create class engine and implements service callback methods
     */
    private class Engine extends CanvasWatchFaceService.Engine {

        /**
         * resoures
         */
        final Resources resources = WatchFace.this.getResources();
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        /**
         * check if time is registered
         */
        boolean mRegisteredTimeZoneReceiver = false;
        /**
         * graphics object
         */
        Bitmap mBackgroundImage;
        Bitmap mBackgroundScaledImage;
        Paint mBackgroundPaint;
        Paint mDatePaint;
        Paint mTitleTextPaint;
        Paint mTextPaint;
        Paint mTimePaint;
        Paint mSecondPaint;
        /**
         * check if wearable is ambient mode
         */
        boolean mAmbient;
        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;
        /**
         * object to take current date
         */
        Time mTime;
        Date mDate;
        /**
         * receiver to update the time zone
         */
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        /**
         * dimensions
         */
        float mXOffsetDay;
        float mYOffsetDay;
        float mXOffsetTime;
        float mYOffsetTime;
        float mXOffsetSecond;
        float mYOffsetSecond;
        float mXOffsetText;
        float mYOffsetText;
        float mYOffsetTitleText;

        /**
         * initialize watch face
         */
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            /**
             *  configure the system ui
             */
            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            /**
             * create graphic styles
             */
            Resources resources = WatchFace.this.getResources();

            /**
             * background image
             */
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.bg2, null);
            mBackgroundImage = ((BitmapDrawable) backgroundDrawable).getBitmap();

            /**
             * background color
             */
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            /**
             *  types of writing
             */
            mTitleTextPaint = createTextPaint(resources.getColor(R.color.digital_title_text));
            mTextPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mTimePaint = createTextPaint(resources.getColor(R.color.digital_time));
            mSecondPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mDatePaint = createTextPaint(resources.getColor(R.color.digital_text));

            mTime = new Time();
            mDate = new Date();
        }


        /**
         * when application was destroy
         */
        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        /**
         * create text to paint hours minutes and seconds
         */
        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        /**
         * watch face became visible or invisible
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                //  mLoadMeetingsHandler.sendEmptyMessage(MSG_LOAD_MEETINGS);
                /** Update time zone in case it changed while we weren't visible. */
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
//                mLoadMeetingsHandler.removeMessages(MSG_LOAD_MEETINGS);
                //              cancelLoadMeetingTask();
                //TODO add snippet code above to take number of meetings
            }

            /** Whether the timer should be running depends on whether we're visible (as well as
             *  whether we're in ambient mode), so we may need to start or stop the timer.
             */
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onSurfaceChanged(
                SurfaceHolder holder, int format, int width, int height) {
            if (mBackgroundScaledImage == null
                    || mBackgroundScaledImage.getWidth() != width
                    || mBackgroundScaledImage.getHeight() != height) {
                mBackgroundScaledImage = Bitmap.createScaledBitmap(mBackgroundImage,
                        width, height, true /* filter */);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            /** Load resources that have alternate values for round watches. */
            boolean isRound = insets.isRound();
            /**
             *  dimensions axes Y
             */
            mYOffsetDay = isRound ? resources.getDimension(R.dimen.digital_y_offset_day_round)
                    : resources.getDimension(R.dimen.digital_y_offset_day);
            mYOffsetTime = isRound ? resources.getDimension(R.dimen.digital_y_offset_time_round)
                    : resources.getDimension(R.dimen.digital_y_offset_time);
            mYOffsetSecond = isRound ? resources.getDimension(R.dimen.digital_y_offset_second_round)
                    : resources.getDimension(R.dimen.digital_y_offset_second);
            mYOffsetTitleText = isRound ? resources.getDimension(R.dimen.digital_y_offset_title_text_round)
                    : resources.getDimension(R.dimen.digital_y_offset_text);
            mYOffsetText = isRound ? resources.getDimension(R.dimen.digital_y_offset_text_round)
                    : resources.getDimension(R.dimen.digital_y_offset_text);

            /**
             *  dimensions axes X
             */
            mXOffsetDay = isRound ? resources.getDimension(R.dimen.digital_x_offset_day_round)
                    : resources.getDimension(R.dimen.digital_x_offset_day);
            mXOffsetTime = isRound ? resources.getDimension(R.dimen.digital_x_offset_time_round)
                    : resources.getDimension(R.dimen.digital_x_offset_time);
            mXOffsetSecond = isRound ? resources.getDimension(R.dimen.digital_x_offset_second_round)
                    : resources.getDimension(R.dimen.digital_x_offset_second);
            mXOffsetText = isRound ? resources.getDimension(R.dimen.digital_x_offset_text_round)
                    : resources.getDimension(R.dimen.digital_x_offset_text);

           /* mXOffset = resources.getDimension(isRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);*/
            float dateSize = resources.getDimension(isRound
                    ? R.dimen.digital_date_size_round : R.dimen.digital_date_size);
            float timeSize = resources.getDimension(isRound
                    ? R.dimen.digital_time_size_ambient_round : R.dimen.digital_time_size);
            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            float secondSize = resources.getDimension(isRound
                    ? R.dimen.digital_second_size_round : R.dimen.digital_second_size);
            mDatePaint.setTextSize(dateSize);
            mTimePaint.setTextSize(timeSize);
            mSecondPaint.setTextSize(secondSize);
            mTitleTextPaint.setTextSize(textSize);
            mTextPaint.setTextSize(textSize);

        }

        /**
         * get device features
         */
        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        /**
         * time changed
         */
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        /**
         * wearable switched between mode
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTitleTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            /** Whether the timer should be running depends on whether we're visible (as well as
             * whether we're in ambient mode), so we may need to start or stop the timer.
             */
            updateTimer();
        }

        /**
         * draw watch face
         */
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /** Draw the background. */
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {

                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
                // TODO
                // canvas.drawBitmap(mBackgroundScaledImage, 0, 0, null);

            }

            /** Draw H:MM in ambient mode or H:MM:SS in interactive mode. */
            mTime.setToNow();

            if (!isInAmbientMode()) {

                SimpleDateFormat mSimpleDateformat = new SimpleDateFormat("E");
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTime(mDate);
                String day = mSimpleDateformat.format(mDate) + " " + mCalendar.get(Calendar.DAY_OF_MONTH);
                canvas.drawText(day, mXOffsetDay, mYOffsetDay, mDatePaint);
                mTimePaint.setTextSize(resources.getDimension(R.dimen.digital_time_size_round));
                String time = String.format("%d:%02d", mTime.hour, mTime.minute);
                String second = String.format("%02d", mTime.second);
                if (mTime.hour < 10) {
                    canvas.drawText(time, mXOffsetTime, mYOffsetTime, mTimePaint);
                    canvas.drawText(second, mXOffsetSecond, mYOffsetSecond, mSecondPaint);
                } else {
                    canvas.drawText(time, mXOffsetTime - 20, mYOffsetTime, mTimePaint);
                    canvas.drawText(second, mXOffsetSecond + 13, mYOffsetSecond, mSecondPaint);
                }
                //TODO text to get from ai
                canvas.drawText("Tutto sotto controllo.", mXOffsetText, mYOffsetTitleText, mTitleTextPaint);
                canvas.drawText("     Buona giornata.", mXOffsetText, mYOffsetText, mTextPaint);

            } else {

                mTimePaint.setTextSize(resources.getDimension(R.dimen.digital_time_size_ambient_round));
                String time = String.format("%d:%02d", mTime.hour, mTime.minute);
                if (mTime.hour < 10)
                    canvas.drawText(time, mXOffsetTime - 15, mYOffsetTime + 50, mTimePaint);
                else
                    canvas.drawText(time, mXOffsetTime - 35, mYOffsetTime + 50, mTimePaint);
            }


        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

    }
}
