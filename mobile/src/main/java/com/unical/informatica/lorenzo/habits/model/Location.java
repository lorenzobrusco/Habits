package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 02/08/2016.
 */
public class Location {

    private static final String HOME = "HOME";
    private static final String WORK = "WORK";
    private float mLongitude;
    private float mLatitude;

    public Location(String...location){
        this.mLongitude = Float.parseFloat(location[0]);
        this.mLatitude = Float.parseFloat(location[1]);
    }

    public float getmLongitude() {
        return mLongitude;
    }

    public float getmLatitude() {
        return mLatitude;
    }

    public void setmLongitude(float mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setmLatitude(float mLatitude) {
        this.mLatitude = mLatitude;
    }
}
