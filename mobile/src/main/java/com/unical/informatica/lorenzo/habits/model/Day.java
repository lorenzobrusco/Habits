package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 02/08/2016.
 */
public class Day {

    private static final String SUNDAY = "SUNDAY";
    private static final String MONDAY = "MONDAY";
    private static final String TUESDAY = "TUESDAY";
    private static final String WEDNESDAY = "WEDNESDAY";
    private static final String THURSDAY = "THURSDAY";
    private static final String FRIDAY = "FRIDAY";
    private static final String SATURDAY = "SATURDAY";
    private final String mDay;

    public Day(String day) {
        this.mDay = day.split("-")[0];
    }

    public String getDayOfWeek() {

        switch (this.mDay) {
            case "dom": {
                return SUNDAY;
            }
            case "Sun": {
                return SUNDAY;
            }
            case "lun": {
                return MONDAY;
            }
            case "Mon": {
                return MONDAY;
            }
            case "mar": {
                return TUESDAY;
            }
            case "Tue": {
                return TUESDAY;
            }
            case "mer": {
                return WEDNESDAY;
            }
            case "Wed": {
                return WEDNESDAY;
            }
            case "gio": {
                return THURSDAY;
            }
            case "Thu": {
                return THURSDAY;
            }
            case "ven": {
                return FRIDAY;
            }
            case "Fri": {
                return FRIDAY;
            }
            case "sab": {
                return SATURDAY;
            }
            case "Sat": {
                return SATURDAY;
            }
            default:
                return null;
        }
    }
}
