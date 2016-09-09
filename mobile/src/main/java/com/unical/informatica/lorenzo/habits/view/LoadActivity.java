package com.unical.informatica.lorenzo.habits.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.services.Record;

/**
 * Created by Lorenzo on 02/09/2016.
 */
public class LoadActivity extends AppCompatActivity {

    private static Runnable runnable;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        final Handler handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getResources().getIdentifier("slide1", "drawable","lorenzo.informatica.unical.com");
                getResources().getIdentifier("slide2", "drawable","lorenzo.informatica.unical.com");
                getResources().getIdentifier("slide3", "drawable","lorenzo.informatica.unical.com");
                getResources().getIdentifier("slide4", "drawable","lorenzo.informatica.unical.com");
                startActivity(new Intent(LoadActivity.this,WelcomeActivity.class));
                finish();
            }
        };
        handler.postDelayed(runnable, 1000);
    }
}
