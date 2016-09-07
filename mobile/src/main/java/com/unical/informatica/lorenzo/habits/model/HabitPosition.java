package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class HabitPosition extends Habit {

    private String TEXT = "";

    public HabitPosition() {
        type = "Position";
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
