package com.unical.informatica.lorenzo.habits.fragment;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.adapter.HabitAdapter;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.model.HabitAction;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;

import java.util.List;


/**
 * Created by Lorenzo on 03/05/2016.
 * This fragment contein start layout
 */
public class StartFragment extends Fragment {

    private FloatingActionButton fab;
    private List<Habit> listHabits;
    private HabitAdapter habitAdapter;
    private ListView listView;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.habits_layout, container, false);
        /**
         * remove this code
         */
        String record = "";
        HabitAction habit = new HabitAction(record);
        habit.appendTEXT(" you don't die");
        HabitsManager.getInstance().addHabits(habit);
        this.listView = (ListView) view.findViewById(R.id.listHabits);
        this.listHabits = HabitsManager.getInstance().getHabits();
        this.habitAdapter = new HabitAdapter(getContext(),this.listHabits);
        this.listView.setAdapter(this.habitAdapter);
        this.textView = (TextView)  view.findViewById(R.id.HabitsIsEmpty);
        if(!this.listHabits.isEmpty())
            this.textView.setVisibility(View.INVISIBLE);
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
