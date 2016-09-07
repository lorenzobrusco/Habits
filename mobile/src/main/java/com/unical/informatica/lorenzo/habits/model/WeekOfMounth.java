package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 03/09/2016.
 */
public class WeekOfMounth {
    private static final String FIRST = "FIRST_WEEK";
    private static final String SECOND = "SECOND_WEEK";
    private static final String THIRD = "THIRD_WEEK";
    private static final String FOURTH = "FOURTH_WEEK";
    private int mWeek;

    public WeekOfMounth(int mWeek) {
        this.mWeek = mWeek;
    }

    public String getWeekOfMounth() {
        switch (mWeek) {
            case 1:
                return FIRST;
            case 2:
                return SECOND;
            case 3:
                return THIRD;
            case 4:
                return FOURTH;
            default:
                return "?";
        }
    }

}
