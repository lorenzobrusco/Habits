package com.unical.informatica.lorenzo.habits.mining;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lorenzo on 01/09/2016.
 */
public class Grouper {

    private static final String LOGFILE = "log";
    private static final String sepator = ";";

    public AbstractMap<String, Integer> groupRecord(Context mContext) {
        final AbstractMap<String, Integer> records = new HashMap<>();
        String logFile = new BuildFile().getFileValue(LOGFILE, mContext);
        String[] split = logFile.split("\\r?\\n");
        for (String string : split) {
            final String realString = chompString(string);
            final StringBuilder builder = new StringBuilder();
            if (builder.validateString(realString)) {
                String location = builder.getLocation(realString);
                final String day = builder.getDay(realString);
                final String time = builder.getTime(realString);
                final String action = builder.getAction(realString);
                final String people = builder.getPeople(realString);
                final String heartrate = builder.getHeartRate(realString);
                final String week = builder.getWeek(realString);
                if (!location.contains("?")) {
                    final String[] locations = location.split("-");
                    location = locations[0];
                }
                if (!(location.contains("?") && action.contains("?") && people.contains("?") && heartrate.contains("?"))) {
                    String record = location + sepator + "?" + sepator + day + sepator + time + sepator + action + sepator + people + sepator + heartrate + sepator + week;
                    if (!records.containsKey(record)) {
                        records.put(record, 1);
                    } else {
                        int number = records.get(record);
                        number++;
                        records.put(record, number);
                    }
                }
            }
        }
        return records;
    }


    private String chompString(final String string) {
        String[] stringToReturn;
        stringToReturn = string.split("\\<");
        stringToReturn = stringToReturn[1].split("\\ >");
        return stringToReturn[0];
    }

}
