package com.unical.informatica.lorenzo.habits;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * Created by Lorenzo on 29/04/2016.
 */
public class TestToShake extends Activity {

    /* variables to detection shake*/
    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAcceleration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        /* sets variables to shake */
        this.mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                /*Vibrator vibrator = (Vibrator) getSystemService(TestToShake.this.VIBRATOR_SERVICE);
                MakePatternToVibrate toVibrate = new MakePatternToVibrate();
                vibrator.vibrate(toVibrate.setupPattern(), -1);*/

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mSensorManager.registerListener(this.mShakeDetector, this.mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mSensorManager.unregisterListener(this.mShakeDetector);
    }
}
