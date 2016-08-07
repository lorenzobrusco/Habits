package com.unical.informatica.lorenzo.habits;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lorenzo on 26/04/2016.
 */
public class MakePatternToVibrate {

    private long[] timeToVibrate;
    private final ExtractTime time;
    private final long HOUR;
    private final long BETWEEN;
    private final long WAIT;
    private final long MINUTE;


    public MakePatternToVibrate(final long hour, final long minute, final long between, final long wait){
        SimpleDateFormat dataFormat = new SimpleDateFormat("hh:mm");
        String formattedDate = dataFormat.format(new Date());//take time with this format HH:MM
        this.time = new ExtractTime(formattedDate);
        this.timeToVibrate = new long[time.sizePattern()];
        this.HOUR = hour;
        this.MINUTE = minute;
        this.BETWEEN = between;
        this.WAIT = wait;
    }

    public long[] setupPattern(){
        this.timeToVibrate[0] = 0;
        for(int i = 1; i < this.time.sizePattern(); i++ ){
            if(i < this.time.endHour()){
                if((i % 2 )!= 0){
                    this.timeToVibrate[i] = HOUR;
                }
                else{
                    this.timeToVibrate[i] = BETWEEN;
                }
            }
            else if(i == this.time.endHour()){
                this.timeToVibrate[i] = WAIT;
            }
            else{
                if((i % 2 )!= 0){
                    this.timeToVibrate[i] = MINUTE;
                }
                else{
                    this.timeToVibrate[i] = BETWEEN;
                }
            }
        }
        return this.timeToVibrate;
    }
}
