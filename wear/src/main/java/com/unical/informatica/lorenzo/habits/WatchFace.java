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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;
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
    private static final String WEAR_TEXT_PATH = "/text";
    private String type = "Nothing to rememeber";
    private String text = "Good day";

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

    private Handler handler = null;
    private static Runnable runnable = null;
    /**
     * to save battery heart rate is detected only every thirtyseconds and then close for others two minutes
     */
    private boolean enableHeartRate = true;
    private int time = 30000;
    private static final long FIFTEENSECONDS = 15000;

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
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                enableOrDisenableHeartRate();
                handler.postDelayed(runnable, time);
            }
        };
        handler.postDelayed(runnable, FIFTEENSECONDS);
        return new Engine();
    }


    @Override
    public void onDestroy() {
        this.mSensorManager.unregisterListener(mShakeDetector);
        this.mSensorManager.unregisterListener(this);
        this.mGoogleApiClient.disconnect();
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
            this.sendMessage(WEAR_MESSAGE_PATH, String.valueOf(this.currentHeartRate));
        } else if (messageEvent.getPath().equalsIgnoreCase(WEAR_TEXT_PATH)) {
            try {
                final String message;
                message = new String(messageEvent.getData(), "UTF-8");
                String[] split = message.split("--");
                this.type = split[0];
                this.text = split[1];
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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

    private void enableOrDisenableHeartRate() {
        if (this.enableHeartRate) {
            this.mSensorManager.unregisterListener(this);
            this.time = 100000;
        } else {
            this.mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            this.mSensorManager.registerListener(this, this.mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            this.time = 30000;
        }
        this.enableHeartRate = !this.enableHeartRate;
    }


    private void setupVibrate() {
        this.mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                /**
                 * enable to shake
                 */
                /*Vibrator vibrator = (Vibrator) getSystemService(WatchFace.this.VIBRATOR_SERVICE);
                MakePatternToVibrate toVibrate = new MakePatternToVibrate(500, 100, 500, 1000);
                vibrator.vibrate(toVibrate.setupPattern(), -1);*/
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

    /**
     * create class engine and implements service callback methods
     */
    private class Engine extends CanvasWatchFaceService.Engine {
        static final int MSG_UPDATE_TIME = 0;

        /**
         * Handler to update the time periodically in interactive mode.
         */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };

        boolean mRegisteredTimeZoneReceiver = false;

        boolean mAmbient;

        Time mTime;

        float mXOffset = 0;
        float mYOffset = 0;

        private Bitmap mBackgroundBitmap;
        private Bitmap mBackgroundScaledBitmap;

        private int specW, specH;
        private View myLayout;
        private TextView day, time, second, title, text;
        private final Point displaySize = new Point();

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            Resources resources = WatchFace.this.getResources();

            Drawable backgroundDrawable = resources.getDrawable(R.drawable.background, null);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            mTime = new Time();

            // Inflate the layout that we're using for the watch face
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myLayout = inflater.inflate(R.layout.watchface_layout, null);

            // Load the display spec - we'll need this later for measuring myLayout
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);

            // Find some views for later use
            day = (TextView) myLayout.findViewById(R.id.day);
            time = (TextView) myLayout.findViewById(R.id.timeText);
            second = (TextView) myLayout.findViewById(R.id.timeSecond);
            title = (TextView) myLayout.findViewById(R.id.title);
            text = (TextView) myLayout.findViewById(R.id.text);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
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
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            // Recompute the MeasureSpec fields - these determine the actual size of the layout
            specW = View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.EXACTLY);
            specH = View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.EXACTLY);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onSurfaceChanged(
                SurfaceHolder holder, int format, int width, int height) {
            if (mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                        width, height, true /* filter */);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;

                // Show/hide the seconds fields
                if (inAmbientMode) {
                    day.setTextColor(getResources().getColor(R.color.white));
                    second.setTextColor(getResources().getColor(R.color.white));
                    title.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);

                } else {
                    day.setTextColor(getResources().getColor(R.color.digital_day));
                    second.setTextColor(getResources().getColor(R.color.digital_second));
                    text.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Get the current Time
            mTime.setToNow();


            // Apply it to the date fields
            day.setText(String.format("%ta", mTime.toMillis(false)) + " " + String.format("%02d", mTime.monthDay));

            time.setText(String.format("%02d", mTime.hour) + ":" + String.format("%02d", mTime.minute));
            second.setText(String.format("%02d", mTime.second));
            title.setText(WatchFace.this.type);
            text.setText(WatchFace.this.text);
            // Update the layout
            myLayout.measure(specW, specH);
            myLayout.layout(0, 0, myLayout.getMeasuredWidth(), myLayout.getMeasuredHeight());

            // Draw it to the Canvas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canvas.drawColor(getResources().getColor(R.color.background, getTheme()));
            } else
                canvas.drawColor(getResources().getColor(R.color.background));
            if (isInAmbientMode())
                canvas.drawColor(Color.BLACK);
            canvas.translate(mXOffset, mYOffset);
            myLayout.draw(canvas);
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
    }
}
