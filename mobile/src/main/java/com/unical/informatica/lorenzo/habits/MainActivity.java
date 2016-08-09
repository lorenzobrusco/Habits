package com.unical.informatica.lorenzo.habits;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;
import com.unical.informatica.lorenzo.habits.utils.PermissionCheckUtils;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {


    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final int REQUEST_ALL_MISSING_PERMISSIONS = 1;
    private GoogleApiClient mGoogleApiClient;
    private Date mDate;
    private Location mLocation;
    private String location = "?";
    private String day = "?";
    private String time = "?";
    private String heart_rate = "?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .addApi(LocationServices.API)
                    .build();
        }
        this.mDate = new Date();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildRecord();
            }
        });


    }

    @Override
    protected void onStart() {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        if (!hasAllRequiredPermissions()) {
            requestAllRequiredPermissions();
        }
        super.onStart();
    }


    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(this.mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ALL_MISSING_PERMISSIONS);
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        this.location = mLocation.getLatitude() + "-" + mLocation.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL_MISSING_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onAllRequiredPermissionsGranted();
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow all permissions", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }


    @SuppressLint("NewApi")
    private void requestAllRequiredPermissions() {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();

        for (String permission : getRequiredPermissions()) {
            if (!PermissionCheckUtils.hasPermission(getApplicationContext(), permission)) {
                notGrantedPermissions.add(permission);
            }
        }

        if (notGrantedPermissions.size() > 0) {
            requestPermissions(notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]),
                    REQUEST_ALL_MISSING_PERMISSIONS);
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

    /**
     * @return All permission which this activity needs
     */
    protected String[] getRequiredPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.BODY_SENSORS
        };
    }

    /**
     * Called when all need permissions granted
     */
    protected void onAllRequiredPermissionsGranted() {
        Toast.makeText(getApplicationContext(), "all permissions are allow", Toast.LENGTH_SHORT).show();
    }

    private boolean hasAllRequiredPermissions() {
        for (String permission : getRequiredPermissions()) {
            if (!PermissionCheckUtils.hasPermission(getApplicationContext(), permission)) {
                return false;
            }
        }
        return true;
    }

    private void sendMessage(final String path, final String message) {
        //Send messago to paired weareble
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(MainActivity.this.mGoogleApiClient).await();
                for (Node node : nodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(MainActivity.this.mGoogleApiClient,
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
        this.sendMessage(WEAR_MESSAGE_PATH, "this is a test");
        this.getTime();
        String string = new StringBuilder().buildTuple(this.location, this.day, this.time, "?", "?", this.heart_rate, "?");
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}




