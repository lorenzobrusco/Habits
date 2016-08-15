package com.unical.informatica.lorenzo.habits.support;

/**
 * handler to buil a string to pass at algorithm such a tuple
 */
public class StringBuilder {

    private final int size = 7;
    private static final int LOCATION = 0;
    private static final int DAY = 1;
    private static final int TIME = 2;
    private static final int ACTION = 3;
    private static final int PEOPLE = 4;
    private static final int HEARTRATE = 5;
    private static final int SPEED = 6;
    private static final String sepator = ", ";
    private String stringToBuild = "";

    public String buildTuple (final String location, final String day, final String time, final String action, final String people, final String heart_rate, final String speed){

        stringToBuild += "< " +location + sepator + day + sepator + time + sepator + action + sepator
                + people + sepator + heart_rate + sepator + speed + " >\n";
        return stringToBuild;
    }

    public boolean validateString(String stringToCheck){
        return stringToCheck.split(sepator).length == size;
    }

    public String getLocation(final String stringToBuild){
        return stringToBuild.split(sepator)[LOCATION];
    }

    public String getDay(final String stringToBuild){
        return stringToBuild.split(sepator)[DAY];
    }

    public String getTime(final String stringToBuild){
        return stringToBuild.split(sepator)[TIME];
    }

    public String getAction(final String stringToBuild){
        return stringToBuild.split(sepator)[ACTION];
    }

    public String getPeople(final String stringToBuild){
        return stringToBuild.split(sepator)[PEOPLE];
    }

    public String getHeartRate(final String stringToBuild){
        return stringToBuild.split(sepator)[HEARTRATE];
    }

    public String getSpeed(final String stringToBuild){
        return stringToBuild.split(sepator)[SPEED];
    }
}
