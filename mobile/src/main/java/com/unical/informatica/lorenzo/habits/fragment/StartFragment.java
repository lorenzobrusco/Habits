package com.unical.informatica.lorenzo.habits.fragment;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unical.informatica.lorenzo.habits.R;


/**
 * Created by Lorenzo on 03/05/2016.
 * This fragment contein start layout
 */
public class StartFragment extends Fragment {

    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.habits_layout, container, false);
        this.fab = (FloatingActionButton) view.findViewById(R.id.fabStartPage);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO add habits
            }
        });
        return view;
    }

}
