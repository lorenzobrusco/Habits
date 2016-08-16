package com.unical.informatica.lorenzo.habits.model;

import com.unical.informatica.lorenzo.habits.support.StringBuilder;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class Habit {

    protected String type;
    private Location mLocation;
    private Day mDay;
    private Time mTime;
    private Action mAction;
    private People mPeople;
    private HeartRate mHeartRate;

    public Habit(final String habit) {
        StringBuilder mStringBuilder = new StringBuilder();
        if (mStringBuilder.validateString(habit)) {
            this.mLocation = new Location(mStringBuilder.getLocation(habit));
            this.mDay = new Day(mStringBuilder.getDay(habit));
            this.mTime = new Time(Integer.parseInt(mStringBuilder.getTime(habit)));
            this.mAction = new Action(mStringBuilder.getAction(habit));
            this.mPeople = new People(mStringBuilder.getPeople(habit));
            this.mHeartRate = new HeartRate(Integer.parseInt(mStringBuilder.getHeartRate(habit)));
        } else {
            //TODO define action when string isn't valide
        }
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public Day getmDay() {
        return mDay;
    }

    public void setmDay(Day mDay) {
        this.mDay = mDay;
    }

    public Time getmTime() {
        return mTime;
    }

    public void setmTime(Time mTime) {
        this.mTime = mTime;
    }

    public Action getmAction() {
        return mAction;
    }

    public void setmAction(Action mAction) {
        this.mAction = mAction;
    }

    public People getmPeople() {
        return mPeople;
    }

    public void setmPeople(People mPeople) {
        this.mPeople = mPeople;
    }

    public HeartRate getmHeartRate() {
        return mHeartRate;
    }

    public void setmHeartRate(HeartRate mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getType() {
        return type;
    }

    public String getTEXT(){ return null;}

    @Override
    public boolean equals(Object obj) {
        return (this.mLocation.getmLatitude() == ((Habit) obj).mLocation.getmLatitude() &&
                this.mLocation.getmLongitude() == ((Habit) obj).mLocation.getmLongitude() &&
                this.mAction.getType() == ((Habit) obj).mAction.getType() && this.mPeople == ((Habit) obj).mPeople &&
                this.mDay.getDayOfWeek() == ((Habit) obj).mDay.getDayOfWeek() && this.mTime.getTime() == ((Habit) obj).mTime.getTime() &&
                this.mHeartRate.typeHeartRate() == ((Habit) obj).mHeartRate.typeHeartRate());
    }
}
