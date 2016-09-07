package com.unical.informatica.lorenzo.habits;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.unical.informatica.lorenzo.habits.adapter.ViewPagerAdapter;
import com.unical.informatica.lorenzo.habits.fragment.LogFragment;
import com.unical.informatica.lorenzo.habits.fragment.SettingFragment;
import com.unical.informatica.lorenzo.habits.fragment.StartFragment;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.mining.Grouper;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.services.Record;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.XMLPullParserHandler;
import com.unical.informatica.lorenzo.habits.utils.PermissionCheckUtils;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private static final String NOTIFICATION = "notification";
    private static final String VIBRATION = "vibration";
    private static final String WEARABLE = "wearable";
    private static final int[] ICON = {R.drawable.ic_settings_white_24dp, R.drawable.ic_subtitles_white_24dp, R.drawable.ic_description_white_24dp};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_views);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setIcon(R.mipmap.ic_info);
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.setupViewPager(viewPager);

        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(viewPager);
        this.setupIconViewPager();

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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "delete all", Toast.LENGTH_SHORT).show();
            return true;

        }*/
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

        final SettingFragment settingFragment = new SettingFragment();
        final StartFragment startFragment = new StartFragment();
        final LogFragment logFragment = new LogFragment();

        adapter.addFragment(settingFragment, "Settings");
        adapter.addFragment(startFragment, "Routines");
        adapter.addFragment(logFragment, "Log");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        HabitsManager.getInstance().setAdapter(this.adapter);
    }

    public static void setupIconViewPager() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(ICON[i]);
        }
    }

}
