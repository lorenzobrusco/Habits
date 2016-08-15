package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 02/08/2016.
 */
public class HeartRate {

    private final String REST = "REST";
    private final String ACTIVITY = "ACTIVITY";
    private final String INTENSIVE = "INTENSIVE";
    private int mHeartRate = 0;

    public HeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String typeHeartRate() {

        if (this.mHeartRate > 50  && this.mHeartRate < 90)
            return REST;
        if (this.mHeartRate >= 90 && this.mHeartRate <= 130)
            return ACTIVITY;
        if( this.mHeartRate > 130 && this.mHeartRate <= 220)
            return INTENSIVE;

        return "?";
    }

}
