package com.unical.informatica.lorenzo.habits.services;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import com.unical.informatica.lorenzo.habits.model.Day;
import com.unical.informatica.lorenzo.habits.model.HeartRate;
import com.unical.informatica.lorenzo.habits.model.Time;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class Record extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String LOGFILE = "log";
    private static final long FIVEMINUTES = 200000;
    private static final long FIFTEENSECONDS = 15000;
    private GoogleApiClient mGoogleApiClient;
    private Date mDate;
    private Location mLocation;
    private String location = "?";
    private String day = "?";
    private String time = "?";
    private String heart_rate = "0";
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public void onCreate() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        this.mDate = new Date();
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                buildRecord();
                reset();
                handler.postDelayed(runnable, FIVEMINUTES);
            }
        };

        handler.postDelayed(runnable, FIFTEENSECONDS);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        this.location = mLocation.getLatitude() + "-" + mLocation.getLongitude();
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


    private void sendMessage(final String path, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(Record.this.mGoogleApiClient).await();
                for (Node node : nodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(Record.this.mGoogleApiClient,
                            node.getId(), path, message.getBytes()).await();
                }
            }
        }).start();
    }

    private void getTime() {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);
        this.day = new SimpleDateFormat("E").format(mDate) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH);
        this.time = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    private void buildRecord() {
        this.getLocation();
        this.sendMessage(WEAR_MESSAGE_PATH, "send heart_rate");
        this.getTime();
        String record = new StringBuilder().buildTuple(this.location, new Day(this.day).getDayOfWeek(), new Time(Integer.parseInt(this.time)).getTime(), "?", "?", new HeartRate(Integer.parseInt(this.heart_rate)).typeHeartRate(), "?");
        BuildFile.getInstance(this.context, LOGFILE).appendFileValue(LOGFILE, record, this.context);
        reset();
    }

    private void reset(){
        this.location = "?";
        this.day = "?";
        this.time = "?";
        this.heart_rate = "0";
    }

}



