package com.unical.informatica.lorenzo.habits;


/**
 * Created by Lorenzo on 26/04/2016.
 */
public class ExtractTime {

    private String timeToExtract;
    private int[] time;

    public ExtractTime(String time){
        this.timeToExtract = time;
        this.time = extract();
    }

    public int[] extract(){
        String[] split= this.timeToExtract.split(":");
        int hour = Integer.parseInt(split[0]);
        if(hour > 12){
            hour -= 12;
        }
        int minute = Integer.parseInt(split[1]);
        if(minute > 5){
            minute /= 5;
        }
        else{
            minute = 1;
        }
        int[] time= {hour, minute};
        return time;

    }

    public int sizePattern(){
        return (this.time[0]*2)+(this.time[1]*2)+2;
    }

    public int endHour(){
        return (this.time[0]*2)+1;
    }

}
