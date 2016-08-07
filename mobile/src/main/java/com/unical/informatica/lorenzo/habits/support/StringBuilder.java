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
    private static final String sepator = "@";
    private String stringToBuild = "";

    public String buildTuple (final String location, final String day, final String time, final String action, final String people, final String heart_rate, final String speed){

        stringToBuild += location + sepator + day + sepator + time + sepator + action + sepator
                + people + sepator + heart_rate + sepator + speed;
        return stringToBuild;
    }

    public boolean validateString(){
        return this.stringToBuild.split(sepator).length == size;
    }

    public String getLocation(){
        return this.stringToBuild.split(sepator)[LOCATION];
    }

    public String getDay(){
        return this.stringToBuild.split(sepator)[DAY];
    }

    public String getTime(){
        return this.stringToBuild.split(sepator)[TIME];
    }

    public String getAction(){
        return this.stringToBuild.split(sepator)[ACTION];
    }

    public String getPeople(){
        return this.stringToBuild.split(sepator)[PEOPLE];
    }

    public String getHeartRate(){
        return this.stringToBuild.split(sepator)[HEARTRATE];
    }

    public String getSpeed(){
        return this.stringToBuild.split(sepator)[SPEED];
    }
}
