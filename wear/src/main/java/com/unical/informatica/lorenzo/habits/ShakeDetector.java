package com.unical.informatica.lorenzo.habits;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * This class is used when needed to detecion a shake of the your weareble
 */
public class ShakeDetector implements SensorEventListener {

    /* minimum acceleration needed to count as a shake movement */
    private static final int MIN_SHAKE_ACCELERATION = 8;

    /* minimum number of the moviment to registera shake */
    private static final int MIN_MOVIMENTS = 4;

    /* maximum time for the whole shake to occur */
    private static final int MAX_SHAKE_DURATION = 500;

    /*arrays to store gravity and acceleration values*/
    private float[] mGravity = {0.0f, 0.0f, 0.0f};
    private float[] mAcceleration = {0.0f, 0.0f, 0.0f};

    /* Components to array */
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    /* this interface will be notificed when the shake is detected*/
    private OnShakeListener mShakeListener;

    /* start time to shake detection */
    private long startTime = 0;

    /* counter for shake moviments */
    private int moveCount = 0;

    /* costructor that sets the shake listener declared down*/
    public ShakeDetector(final OnShakeListener onShakeListener){
        this.mShakeListener = onShakeListener;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        /* this method will be called when the accelerometer detects a change*/

        this.setCurrentAcceleration(event);

        /* get the max linear acceleration in any direction*/
        final float maxLinearAcceleration = this.getMaxCurrentLinearAcceleration();

        /* check if the acceleration is greater than out minimum threshold */
        if(maxLinearAcceleration > MIN_SHAKE_ACCELERATION){

            /* take the current time */
            long now = System.currentTimeMillis();

            /* check if the start time is 0*/
            if(this.startTime == 0){
                /* set current time at the now*/
                this.startTime = now;
            }

            long elapsedTime = now - this.startTime;

            /* check if we're still shake*/
            if(elapsedTime > MAX_SHAKE_DURATION){
                /* too much time has passed. Start over*/
                this.resetShakeDetection();
            } else{
              /* keep track of all moviments*/
                this.moveCount ++ ;

                /* check if enough moviments have been made to qualify as a shake*/
                if(this.moveCount > MIN_MOVIMENTS){
                    /* it's a shake, notify the listener*/
                    this.mShakeListener.onShake();

                    /*reset for the next step*/
                    this.resetShakeDetection();
                }
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void setCurrentAcceleration(SensorEvent sensor){
        /*this method accounts for gravity using a high-pass filter,
         * this code is taked form android developer site */

        final float alpha = 0.8f;

        /* gravity components of x, y and z accelaration*/
        this.mGravity[X] = alpha * mGravity[X] + (1 - alpha) * sensor.values[X];
        this.mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * sensor.values[Y];
        this.mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * sensor.values[Z];

        /* linear acceleration along the x,y and z axes*/
        this.mAcceleration[X] = sensor.values[X] - this.mGravity[X];
        this.mAcceleration[Y] = sensor.values[Y] - this.mGravity[Y];
        this.mAcceleration[Z] = sensor.values[Z] - this.mGravity[Z];

    }

    private float getMaxCurrentLinearAcceleration(){
        /* start by setting the value to the x value*/
        float maxLinearAcceleration = this.mAcceleration[X];

        /* check if the y value is greater*/
        if(this.mAcceleration[Y] > maxLinearAcceleration){
            maxLinearAcceleration = this.mAcceleration[Y];
        }

        /* check if the z value is greater*/
        if(this.mAcceleration[Z] > maxLinearAcceleration){
            maxLinearAcceleration = this.mAcceleration[Z];
        }

        /* return greatest value*/
        return maxLinearAcceleration;
    }

    private void resetShakeDetection(){
        this.startTime = 0;
        this.moveCount = 0;
    }

    /* create a interface that will has only a methods */
    public interface OnShakeListener {
        public void onShake();
    }

}
