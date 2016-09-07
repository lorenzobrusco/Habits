package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 18/08/2016.
 */
public class HabitOther extends Habit {

    private String TEXT = "";

    public HabitOther() {
        type = "Other";
        prefix = "Remember to:";
    }

    @Override
    public String getText() {
        return TEXT;
    }

    @Override
    public void setText(String toAppend) {
        this.TEXT = toAppend;
    }
}
