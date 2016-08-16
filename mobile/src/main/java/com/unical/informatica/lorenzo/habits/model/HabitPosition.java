package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class HabitPosition extends Habit {

    private String TEXT  = "Today you didn't go: ";

    public HabitPosition(String habit) {
        super(habit);
        type = "Position";
    }

    @Override
    public String getTEXT() {
        return TEXT;
    }

    public void appendTEXT(String toAppend) {
        this.TEXT += TEXT;
    }
}
