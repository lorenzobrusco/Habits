package com.unical.informatica.lorenzo.habits;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.unical.informatica.lorenzo.habits.adapter.ViewPagerAdapter;
import com.unical.informatica.lorenzo.habits.fragment.LogFragment;
import com.unical.informatica.lorenzo.habits.fragment.SettingFragment;
import com.unical.informatica.lorenzo.habits.fragment.StartFragment;
import com.unical.informatica.lorenzo.habits.services.Record;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.utils.PermissionCheckUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ALL_MISSING_PERMISSIONS = 1;
    private static final String LOGFILE = "log";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_views);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setNavigationIcon(null);
        this.setSupportActionBar(toolbar);

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.setupViewPager(viewPager);

        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(viewPager);

        startService(new Intent(this,Record.class));
    }

    @Override
    protected void onStart() {
        if (!hasAllRequiredPermissions()) {
            requestAllRequiredPermissions();
        }
        super.onStart();
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
/*        int id = item.getItemId();
        if (id == R.id.action_settings) {
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

    private void setupViewPager(ViewPager viewPager) {
        /** setup the page in the view page*/
        this.adapter = new ViewPagerAdapter(getSupportFragmentManager());

        final SettingFragment settingFragment = new SettingFragment();
        final StartFragment startFragment = new StartFragment();
        final LogFragment logFragment = new LogFragment();

        adapter.addFragment(settingFragment, "Settings");
        adapter.addFragment(startFragment, "Start");
        adapter.addFragment(logFragment, "Log");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

}




