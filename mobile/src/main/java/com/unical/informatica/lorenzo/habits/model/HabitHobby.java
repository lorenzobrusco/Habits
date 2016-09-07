package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 17/08/2016.
 */
public class HabitHobby extends Habit {

    private String TEXT = "";

    public HabitHobby() {
        type = "Hobby";
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
