package com.unical.informatica.lorenzo.habits;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Lorenzo on 21/04/2016.
 */
public class WearMessageListenerService extends WearableListenerService {

    //Start declaration of the strings used to comunicate with the paired wearable
    private static final String START_ACTIVITY = "/start_activity";
    //End declaration of the strings

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            Intent intent = new Intent( this, MainActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
