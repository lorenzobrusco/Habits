package com.unical.informatica.lorenzo.habits.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.view.CustomDialogPrivacy;
import com.unical.informatica.lorenzo.habits.view.WelcomeActivity;

/**
 * Created by Lorenzo on 14/08/2016.
 */
public class SettingFragment extends Fragment {

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.setting_layout, container, false);
        this.notfication = (Switch) view.findViewById(R.id.EnableNotification);
        this.vibration = (CheckBox) view.findViewById(R.id.EnableVibration);
        this.wearable = (Switch) view.findViewById(R.id.EnableWear);
        this.welcomeSlide = (LinearLayout) view.findViewById(R.id.welcomePage);
        this.privacy = (LinearLayout) view.findViewById(R.id.privacy);
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
                SharedPreferences settings = getContext().getSharedPreferences(PREF_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(IS_FIRST_TIME_LAUNCH, true);
                editor.commit();
                startActivity(new Intent(getActivity(),WelcomeActivity.class));
            }
        });
        this.privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildPrivacyDialog();
            }
        });
        return view;

    }

    private void buildPrivacyDialog(){
      final CustomDialogPrivacy customDialogPrivacy = new CustomDialogPrivacy(this.getContext());
        customDialogPrivacy.show();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
            this.notfication.setChecked(HabitsManager.getInstance().isNotification());
            this.vibration.setChecked(HabitsManager.getInstance().isVibration());
            this.wearable.setChecked(HabitsManager.getInstance().isWearable());
        }
    }
}