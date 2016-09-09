package com.unical.informatica.lorenzo.habits.manager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.adapter.ViewPagerAdapter;
import com.unical.informatica.lorenzo.habits.model.Day;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.model.HabitAction;
import com.unical.informatica.lorenzo.habits.model.HabitHobby;
import com.unical.informatica.lorenzo.habits.model.HabitOther;
import com.unical.informatica.lorenzo.habits.model.HabitPosition;
import com.unical.informatica.lorenzo.habits.model.HeartRate;
import com.unical.informatica.lorenzo.habits.model.Time;
import com.unical.informatica.lorenzo.habits.model.WeekOfMounth;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;
import com.unical.informatica.lorenzo.habits.view.LoadActivity;
import com.unical.informatica.lorenzo.habits.view.WelcomeActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Lorenzo on 14/08/2016.
 */
public class HabitsManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String WEAR_TEXT_PATH = "/text";
    private static final String LOGFILE = "log";
    private static final String TAG = "HABITSMANAGER";
    private GoogleApiClient mGoogleApiClient;
    private Date mDate;
    private Location mLocation;
    private String location = "?";
    private String currentDate = "?";
    private String day = "?";
    private String time = "?";
    private String heart_rate = "0";
    private String weekOfMounth = "?";
    private int notifyID = 1;
    private int mId = 001;
    private static HabitsManager mHabitsManager;
    private ViewPagerAdapter adapter;
    private boolean changed = false;
    private boolean notification = false;
    private boolean vibration = false;
    private boolean wearable = true;
    private List<Habit> habits;

    public HabitsManager() {
        this.habits = new ArrayList<>();
    }

    public static HabitsManager getInstance() {
        if (mHabitsManager == null)
            mHabitsManager = new HabitsManager();
        return mHabitsManager;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(this.mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connected", "failed connected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connected", "failed connected");
    }

    private void getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLocation != null) {
            this.location = this.getAddress(context, mLocation.getLatitude(), mLocation.getLongitude());
            this.location += " - Langitudine:" + mLocation.getLatitude() + ", Longitude:" + mLocation.getLongitude();
        } else if (mLocation == null || this.location.contains("null")) {
            this.location = "?";
        }
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
            try {
                final String message;
                message = new String(messageEvent.getData(), "UTF-8");
                this.heart_rate = message;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRoutines(String routines) {
        if (!isWearable())
            return;
        this.sendMessage(WEAR_TEXT_PATH, routines);
    }

    private String getAddress(Context mContext, double lat, double lon) {
        try {
            Geocoder geocoder;
            String addressTMP = "";
            List<Address> addresses;
            geocoder = new Geocoder(mContext, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addressTMP += addresses.get(0).getLocality();
            addressTMP += ", ";
            addressTMP += addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            return addressTMP;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendMessage(final String path, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(HabitsManager.this.mGoogleApiClient).await();
                for (Node node : nodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(HabitsManager.this.mGoogleApiClient,
                            node.getId(), path, message.getBytes()).await();
                }
            }
        }).start();
    }

    private void getCurrentDate() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        this.currentDate = df.format(Calendar.getInstance().getTime());
    }

    private void getTime() {
        this.mDate = new Date();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);
        this.day = new SimpleDateFormat("E").format(mDate) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH);
        this.time = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        this.weekOfMounth = String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_MONTH));
    }

    private void reset() {
        this.location = "?";
        this.day = "?";
        this.time = "?";
        this.currentDate = "?";
        this.heart_rate = "0";
    }

    public void connection(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        }
    }

    public void disconnection() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public String getNameTime() {
        this.getTime();
        return new Time(Integer.parseInt(this.time)).getTime();
    }

    public String getDay() {
        this.getTime();
        return new Day(this.day).getDayOfWeek();
    }

    public void buildRecord(Context context) {
        this.getLocation(context);
        this.sendMessage(WEAR_MESSAGE_PATH, "send heart_rate");
        this.getTime();
        this.getCurrentDate();
        String record = new StringBuilder().buildTuple(this.location, this.currentDate, new Day(this.day).getDayOfWeek(), new Time(Integer.parseInt(this.time)).getTime(), "?", "?", new HeartRate(Integer.parseInt(this.heart_rate)).typeHeartRate(),
                new WeekOfMounth(Integer.parseInt(this.weekOfMounth)).getWeekOfMounth());
        new BuildFile().appendFileValue(LOGFILE, record, context);
        reset();
    }

    public void buildNotify(Context mContext, Habit habit) {
        if (!this.isNotification())
            return;
        int image = R.mipmap.ic_launcher_ic;
  /*      if (habit instanceof HabitPosition)
            image = R.drawable.position;
        else if (habit instanceof HabitAction)
            image = R.drawable.action;
        else if (habit instanceof HabitHobby)
            image = R.drawable.hobby;
        else if (habit instanceof HabitOther)
            image = R.drawable.hobby;
*/
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher_ic)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                mContext.getResources(), image))
                        .setContentTitle(habit.getPrefix())
                        .setContentText(habit.getText())
                        .setAutoCancel(true);
        Intent resultIntent = new Intent(mContext, WelcomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(LoadActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());

    }

    public void buildNotifyAI(Context mContext, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                mContext.getResources(), R.drawable.ai))
                        .setContentTitle("I supposed:")
                        .setContentText(text)
                        .setAutoCancel(true);
        Intent resultIntent = new Intent(mContext, WelcomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(WelcomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void vibrate(Context mContext, long time) {
        if (!isVibration())
            return;
        Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    public List<Habit> getCurrentHabit() {
        List<Habit> habitsTMP = new ArrayList<>();
        this.getTime();

        for (Habit habit : this.habits) {
            if (habit.getmDay().equalsIgnoreCase("Everyday") || habit.getmDay().equalsIgnoreCase(new Day(this.day).getDayOfWeek())) {
                if (habit.getmTime().equalsIgnoreCase(new Time(Integer.parseInt(this.time)).getTime())) {
                    if (!habit.isVisited()) {
                        habitsTMP.add(habit);
                    }
                }
            } else {
                habit.setVisited(false);
            }
        }
        this.reset();
        return habitsTMP;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public boolean isWearable() {
        return wearable;
    }

    public void setWearable(boolean wearable) {
        this.wearable = wearable;
    }

    public void addHabits(Habit habit) {
        this.habits.add(habit);
    }

    public void deleteHabit(Habit habit) {
        this.habits.remove(habit);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public List<Habit> getHabits(final String day) {
        List<Habit> tmp = new ArrayList<>();
        for (Habit habit : this.habits) {
            if (habit.getmDay().equalsIgnoreCase(day) || habit.getmDay().equalsIgnoreCase("everyday"))
                tmp.add(habit);
        }
        return tmp;
    }

    public List<Habit> getHabitsToSave() {
        return this.habits;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
    }

    public Habit getHabit(int index) {
        return this.habits.get(index);
    }

    public ViewPagerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ViewPagerAdapter adapter) {
        this.adapter = adapter;
    }
}
