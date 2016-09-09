package com.unical.informatica.lorenzo.habits.services;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.mining.Grouper;
import com.unical.informatica.lorenzo.habits.mining.MiningPosition;
import com.unical.informatica.lorenzo.habits.model.Day;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.model.HeartRate;
import com.unical.informatica.lorenzo.habits.model.Time;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class Record extends Service {

    private static final long FIVEMINUTES = 200000;
    private static final long FIFTEENSECONDS = 15000;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DEFAULT = "MyPrefsFile";
    private static final String DAILY = "daily";
    private Handler handler = null;
    private static Runnable runnable = null;
    private String currentTime = "";
    private int index = 0;


    @Override
    public void onCreate() {
        HabitsManager.getInstance().connection(this);
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                HabitsManager.getInstance().buildRecord(Record.this);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (!currentTime.equals(HabitsManager.getInstance().getNameTime())) {
                    currentTime = HabitsManager.getInstance().getNameTime();
                    HabitsManager.getInstance().sendRoutines(DEFAULT);
                    index = 0;
                }
                if (!HabitsManager.getInstance().getCurrentHabit().isEmpty()) {
                    Habit habit = HabitsManager.getInstance().getCurrentHabit().get(index);
                    habit.setVisited(true);
                    if (HabitsManager.getInstance().getCurrentHabit().size() > 1)
                        index = ((index + 1) % HabitsManager.getInstance().getCurrentHabit().size());
                    else
                        index = 0;
                    HabitsManager.getInstance().buildNotify(Record.this, habit);
                    HabitsManager.getInstance().vibrate(Record.this, 300);
                    HabitsManager.getInstance().sendRoutines("Remember to "+ "--" + habit.getText());
                }
                if(currentTime.equals("MIDDAY") && settings.getBoolean(DAILY,false)){
                    editor.putBoolean(DAILY, true);
                    final Grouper grouper = new Grouper();
                    final MiningPosition miningPosition = new MiningPosition(grouper.groupRecord(Record.this));
                    miningPosition.analyzeRecordHome(Record.this);
                    miningPosition.analyzeRecordDaily(Record.this);
                } else{
                    editor.putBoolean(DAILY, false);
                }
                handler.postDelayed(runnable, FIVEMINUTES);
            }
        };

        handler.postDelayed(runnable, FIFTEENSECONDS);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        HabitsManager.getInstance().disconnection();
        handler.removeCallbacks(runnable);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}



