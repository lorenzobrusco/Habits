package com.unical.informatica.lorenzo.habits.manager;

import android.util.Log;

import com.unical.informatica.lorenzo.habits.model.Habit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Lorenzo on 14/08/2016.
 */
public class HabitsManager {

    private static HabitsManager mHabitsManager;
    private static final String TAG = "HABITSMANAGER";
    private Collection<Habit> habits;

    public HabitsManager() {
        this.habits = new ArrayList<>();
    }

    public static HabitsManager getInstance() {
        if (mHabitsManager == null)
            mHabitsManager = new HabitsManager();
        return mHabitsManager;
    }

    public void addHabits(Habit habit) {
        if (!this.habits.contains(habit)) {
            this.habits.add(habit);
        } else {
            Log.d(TAG, "habits is exist");
        }
    }

    public void deleteHabit(Habit habit) {
        if (this.habits.contains(habit)) {
            this.habits.remove(habit);
        } else {
            Log.d(TAG, "habits isn't exist");
        }
    }

    public final Collection<Habit> getHabits() {
        return this.habits;
    }

    public boolean isEmpty(){
        return this.habits.isEmpty();
    }

    public int getSize(){
        return this.habits.size();
    }

}
