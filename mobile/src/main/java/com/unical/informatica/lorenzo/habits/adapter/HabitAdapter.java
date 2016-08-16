package com.unical.informatica.lorenzo.habits.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.model.Habit;

import java.util.List;


/**
 * Created by Lorenzo on 16/08/2016.
 */
public class HabitAdapter extends BaseAdapter {

    private Context mContext;
    private List<Habit> mHabits;

    public HabitAdapter(final Context context, final List<Habit> habits) {
        this.mContext = context;
        this.mHabits = habits;
    }

    public void setHabits(final List<Habit> habits) {
        this.mHabits = habits;
    }

    @Override
    public int getCount() {
        return this.mHabits.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mHabits.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.habit_row_layout, null);
        }
        Habit habit = (Habit) getItem(i);
        TextView textView = (TextView) view.findViewById(R.id.contextRowHabitsTitle);
        textView.setText(habit.getType());
        textView = (TextView) view.findViewById(R.id.contexIntoHabitRow);
        textView.setText(habit.getTEXT());
        return view;

    }
}
