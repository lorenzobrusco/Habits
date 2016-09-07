package com.unical.informatica.lorenzo.habits.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.MainActivity;
import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.support.BuildFile;

/**
 * Created by Lorenzo on 14/08/2016.
 */
public class LogFragment extends Fragment {

    private static final String LOGFILE = "log";
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.log_layout, container, false);

        mTextView = (TextView) view.findViewById(R.id.log);
        mTextView.setText(new BuildFile().getFileValue(LOGFILE,LogFragment.this.getActivity()));
        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Log File");
        }
    }

    @Override
    public void onResume() {
        mTextView.setText(new BuildFile().getFileValue(LOGFILE,LogFragment.this.getActivity()));
        super.onResume();
    }
}
