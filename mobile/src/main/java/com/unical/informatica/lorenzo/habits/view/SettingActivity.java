package com.unical.informatica.lorenzo.habits.view;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;

/**
 * Created by Lorenzo on 07/09/2016.
 */
public class SettingActivity extends AppCompatActivity {

    private Switch notfication;
    private CheckBox vibration;
    private Switch wearable;
    private LinearLayout welcomeSlide;
    private LinearLayout privacy;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String PREF_NAME = "androidhive-welcome";
    private static final String NOTIFICATION = "notification";
    private static final String VIBRATION = "vibration";
    private static final String WEARABLE = "wearable";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_setting_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSetting);
        this.setSupportActionBar(toolbar);
        toolbar.setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.notfication = (Switch) findViewById(R.id.EnableNotification);
        this.vibration = (CheckBox) findViewById(R.id.EnableVibration);
        this.wearable = (Switch) findViewById(R.id.EnableWear);
        this.welcomeSlide = (LinearLayout) findViewById(R.id.welcomePage);
        this.privacy = (LinearLayout) findViewById(R.id.privacy);
        this.notfication.setChecked(HabitsManager.getInstance().isNotification());
        this.vibration.setChecked(HabitsManager.getInstance().isVibration());
        this.wearable.setChecked(HabitsManager.getInstance().isWearable());
        this.notfication.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HabitsManager.getInstance().setNotification(b);
            }
        });
        this.vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HabitsManager.getInstance().setVibration(b);
            }
        });
        this.wearable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HabitsManager.getInstance().setWearable(b);
            }
        });
        this.welcomeSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = SettingActivity.this.getSharedPreferences(PREF_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(IS_FIRST_TIME_LAUNCH, true);
                editor.commit();
                startActivity(new Intent(SettingActivity.this, WelcomeActivity.class));
            }
        });
        this.privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildPrivacyDialog();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void buildPrivacyDialog() {
        final CustomDialogPrivacy customDialogPrivacy = new CustomDialogPrivacy(SettingActivity.this);
        customDialogPrivacy.show();
    }
}

