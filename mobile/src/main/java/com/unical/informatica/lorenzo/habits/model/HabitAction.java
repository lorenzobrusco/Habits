package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class HabitAction extends Habit{

    private String TEXT = "Remember to: ";

    public HabitAction(String habit) {
        super(habit);
        type = "Action";
    }

    @Override
    public String getTEXT() {
        return TEXT;
    }

    public void appendTEXT(String toAppend) {
        this.TEXT += toAppend;
    }
}
