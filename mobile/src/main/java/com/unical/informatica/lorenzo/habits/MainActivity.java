package com.unical.informatica.lorenzo.habits;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.unical.informatica.lorenzo.habits.adapter.ViewPagerAdapter;
import com.unical.informatica.lorenzo.habits.fragment.StartFragment;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.services.Record;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.XMLPullParserHandler;
import com.unical.informatica.lorenzo.habits.utils.PermissionCheckUtils;
import com.unical.informatica.lorenzo.habits.view.LogFile;
import com.unical.informatica.lorenzo.habits.view.SettingActivity;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ALL_MISSING_PERMISSIONS = 1;
    private static final String LOGFILE = "log";
    private static final String HABITSFILE = "habits.xml";
    private static final String PREFS_NAME = "MyPrefsFile";
    private boolean doubleBackToExitPressedOnce = false;
    private Toolbar toolbar;
    private static TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView mTextView;
    private Tracker mTracker;
    private static final String NOTIFICATION = "notification";
    private static final String VIBRATION = "vibration";
    private static final String WEARABLE = "wearable";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_views);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.setupViewPager(viewPager);

        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(viewPager);

        mTracker = this.getDefaultTracker();
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        HabitsManager.getInstance().setNotification(settings.getBoolean(NOTIFICATION, false));
        HabitsManager.getInstance().setVibration(settings.getBoolean(VIBRATION, false));
        HabitsManager.getInstance().setWearable(settings.getBoolean(WEARABLE, false));

    }

    @Override
    protected void onStart() {
        this.readHabitsFile();
        if (!hasAllRequiredPermissions()) {
            requestAllRequiredPermissions();
        }else{
            startService(new Intent(this, Record.class));
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.updateHabitsFile();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(NOTIFICATION, HabitsManager.getInstance().isNotification());
        editor.putBoolean(VIBRATION, HabitsManager.getInstance().isVibration());
        editor.putBoolean(WEARABLE, HabitsManager.getInstance().isWearable());

        // Commit the edits!
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        } else if (id == R.id.action_log) {
            Intent intent = new Intent(this,LogFile.class);
            startActivity(new Intent(this,LogFile.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * @return All permission which this activity needs
     */
    protected String[] getRequiredPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.ACCESS_NETWORK_STATE
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

    private void readHabitsFile() {
        try {
            HabitsManager.getInstance().setHabits(new XMLPullParserHandler().read(this.openFileInput(HABITSFILE)));
        } catch (IOException e) {
            Log.i("PARSERXML", "no file found");
            HabitsManager.getInstance().setHabits(new ArrayList<Habit>());
        }
    }

    private void updateHabitsFile() {
        try {
            new BuildFile().deleteFile(HABITSFILE, this);
            new BuildFile().appendFileValue(HABITSFILE, new XMLPullParserHandler().writeAll(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        /** setup the page in the view page*/
        this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StartFragment().setDay("SUNDAY"), "SUNDAY");
        adapter.addFragment(new StartFragment().setDay("MONDAY"), "MONDAY");
        adapter.addFragment(new StartFragment().setDay("TUESDAY"), "TUESDAY");
        adapter.addFragment(new StartFragment().setDay("WEDNESDAY"), "WEDNESDAY");
        adapter.addFragment(new StartFragment().setDay("THURSDAY"), "THURSDAY");
        adapter.addFragment(new StartFragment().setDay("FRIDAY"), "FRIDAY");
        adapter.addFragment(new StartFragment().setDay("SATURDAY"), "SATURDAY");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(this.getDayOfWeeks(HabitsManager.getInstance().getDay()));
        HabitsManager.getInstance().setAdapter(adapter);
    }

    private int getDayOfWeeks(String day) {
        switch (day) {
            case "SUNDAY":
                return 0;
            case "MONDAY":
                return 1;
            case "TUESDAY":
                return 2;
            case "WEDNESDAY":
                return 3;
            case "THURSDAY":
                return 4;
            case "FRIDAY":
                return 5;
            case "SATURDAY":
                return 6;
            default:
                return -1;
        }
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

   /* public static void setupIconViewPager() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(ICON[i]);
        }
    }*/

}
