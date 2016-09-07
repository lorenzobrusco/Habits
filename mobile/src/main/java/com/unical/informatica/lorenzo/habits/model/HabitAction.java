package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class HabitAction extends Habit {

    private String TEXT = "";

    public HabitAction() {
        type = "Action";
        prefix ="Remember to:";
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
