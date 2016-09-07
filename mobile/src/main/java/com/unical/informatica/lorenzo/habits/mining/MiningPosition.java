package com.unical.informatica.lorenzo.habits.mining;

import android.content.Context;

import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorenzo on 02/09/2016.
 */
public class MiningPosition {

    private List<String> positions;
    private boolean[] dayOfWeeks;
    private static final int MINRECORDREPEATE = 6;
    private static final int MINDAYREPEATE = 4;
    private static final int MINWEEKREPEATE = 3;
    private AbstractMap<String, Integer> records;
    private StringBuilder stringBuilder;

    public MiningPosition(AbstractMap<String, Integer> records) {
        this.records = records;
        this.dayOfWeeks = new boolean[7];
        this.positions = new ArrayList<>();
        this.stringBuilder = new StringBuilder();
    }

    public void analyzeRecordHome(Context mContext) {
        final List<String> possibleHabits = new ArrayList<>();
        for (String position : positions) {
            if (stringBuilder.getTime(position).contains("MORNING") || stringBuilder.getTime(position).contains("NIGHT")) {
                int cont = 0;
                for (String key : this.records.keySet()) {
                    if (position.equals(key))
                        continue;
                    final String place = stringBuilder.getLocation(key);
                    final String day = stringBuilder.getDay(key);
                    final String time = stringBuilder.getTime(key);
                    if (this.records.get(key) > MINRECORDREPEATE) {
                        if (time.contains("MORNING") || time.contains("NIGHT")) {
                            if (place.equals(stringBuilder.getLocation(position)) && time.equals(stringBuilder.getTime(position))) {
                                cont++;
                            }
                        } else
                            continue;
                    }
                    if (cont > MINDAYREPEATE) {
                        possibleHabits.add(key);
                    }

                }
            } else
                continue;
        }
        if (!possibleHabits.isEmpty())
            HabitsManager.getInstance().buildNotifyAI(mContext, this.getBestHabit(possibleHabits) + " is your house");
    }

    public void analyzeRecordDaily(Context mContext) {
        for (String position : positions) {
            int cont = 0;
            for (String key : this.records.keySet()) {
                if (position.equals(key))
                    continue;
                final String place = stringBuilder.getLocation(key);
                final String day = stringBuilder.getDay(key);
                final String time = stringBuilder.getTime(key);
                if (this.records.get(key) > MINRECORDREPEATE) {
                    if (place.equals(stringBuilder.getLocation(position)) && time.equals(stringBuilder.getTime(position))) {
                        cont++;
                    }
                } else
                    continue;
                if (cont > MINDAYREPEATE)
                    HabitsManager.getInstance().buildNotifyAI(mContext, "every " + time.toLowerCase() + " you go" + place);
            }
        }
    }

    public void analyzeRecordWeekly(Context mContext) {
        //TODO fix it
        for (String position : positions) {
            int cont = 0;
            for (String key : this.records.keySet()) {
                if (position.equals(key))
                    continue;
                final String place = stringBuilder.getLocation(key);
                final String day = stringBuilder.getDay(key);
                final String time = stringBuilder.getTime(key);
                if (place.equals(stringBuilder.getLocation(position)) && day.equals(stringBuilder.getDay(position))) {
                    cont++;
                } else
                    continue;
            }
            if (cont > MINWEEKREPEATE) ;
            //TODO maydbe it's a habit
        }
    }

    private void extractDay() {
        for (String key : this.records.keySet()) {
            this.positions.add(stringBuilder.getDay(key));
        }
    }

    private int getDayOfWeeks(String day) {
        switch (day) {
            case "SUNDAY":
                return 0;
            case "MONDAY":
                return 1;
            case "TUESDAY":
                return 2;
            case "WEDNESDAY":
                return 3;
            case "THURSDAY":
                return 4;
            case "FRIDAY":
                return 5;
            case "SATURDAY":
                return 6;
            default:
                return -1;
        }
    }

    private String getBestHabit(final List<String> habits) {
        int max = records.get(habits.get(0));
        int index = 0;
        for (int i = 1; i < habits.size(); i++) {
            if (records.get(habits.get(i)) > max) {
                max = records.get(habits.get(i));
                index = i;
            }
        }
        return stringBuilder.getLocation(habits.get(index));
    }

}
