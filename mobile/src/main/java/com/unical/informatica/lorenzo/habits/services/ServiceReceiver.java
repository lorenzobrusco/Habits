package com.unical.informatica.lorenzo.habits.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.unical.informatica.lorenzo.habits.MainActivity;

/**
 * handler to register a new tuple when arrive a phone call
 */
public class ServiceReceiver extends BroadcastReceiver {

    /*@Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                Toast.makeText(context,"call from " + incomingNumber, Toast.LENGTH_SHORT).show();
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }*/


   /* @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w("MY_DEBUG_TAG", state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras
                        .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.w("MY_DEBUG_TAG", phoneNumber);
            }
        }
    }*/

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        // TELEPHONY MANAGER class object to register one listner
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        //Create Listner
        MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

        // Register listener for LISTEN_CALL_STATE
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

             Log.d("MyPhoneListener",state+"   incoming no:"+incomingNumber);
            // state = 1 means when phone is ringing
            if (state == 1) {

                String msg = " New Phone Call Event. Incomming Number : "+incomingNumber;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(ServiceReceiver.this.context, msg, duration);
                toast.show();

            }
        }
    }

}