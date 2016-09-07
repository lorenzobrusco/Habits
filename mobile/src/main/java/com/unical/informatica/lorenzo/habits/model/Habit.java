package com.unical.informatica.lorenzo.habits.model;

/**
 * Created by Lorenzo on 10/08/2016.
 */
public class Habit {

    protected String type = "";
    protected String prefix = "";
    private String mDay = "";
    private String mTime = "";
    private String id = "";
    private boolean visited = false;

    public Habit() {
    }

    public void createID() {
        this.id = type + mDay + mTime + getText().hashCode();
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getmDay() {
        return mDay;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return null;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setText(String toAppend) {
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((Habit) obj).getID());
    }
}
