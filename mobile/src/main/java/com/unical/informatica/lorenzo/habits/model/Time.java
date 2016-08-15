package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 02/08/2016.
 */
public class Time {

    private final String EARLY_MORNING = "EARLY MORNING";
    private final String MORNING = "MORNING";
    private final String MIDDAY = "MIDDAY";
    private final String AFTERNOON = "AFTERNOON";
    private final String LATE_AFTERNOON = "LATE AFTERNOON";
    private final String NIGHT = "NIGHT";
    private int mTime;

    public Time(final int time) {
        this.mTime = time;
    }

    public String getTime() {
        if (this.mTime >= 0 && this.mTime < 6) {
            return EARLY_MORNING;
        } else if (this.mTime >= 6 && this.mTime < 12) {
            return MORNING;
        } else if (this.mTime >= 12 && this.mTime < 15) {
            return MIDDAY;
        } else if (this.mTime >= 15 && this.mTime < 18) {
            return AFTERNOON;
        } else if (this.mTime >= 18 && this.mTime < 20) {
            return LATE_AFTERNOON;
        } else
            return NIGHT;
    }


}
