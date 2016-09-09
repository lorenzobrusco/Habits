package com.unical.informatica.lorenzo.habits.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.support.BuildFile;

/**
 * Created by Lorenzo on 07/09/2016.
 */
public class LogFile extends AppCompatActivity {

    private static final String LOGFILE = "log";
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_log_layout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLog);
        this.setSupportActionBar(toolbar);
        toolbar.setTitle("Log");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTextView = (TextView) findViewById(R.id.log);
        String log = new BuildFile().getFileValue(LOGFILE, this);
        if(log == null)
            log = "";
        mTextView.setText(log);
    }

}
