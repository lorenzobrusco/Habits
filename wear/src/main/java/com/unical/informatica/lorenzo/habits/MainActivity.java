package com.unical.informatica.lorenzo.habits;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {


    //Start declaration of the strings used to comunicate with the paired wearable
    private static final String WEAR_MESSAGE_PATH = "/message";
    //End declaration of the strings

    //Start declaration of the time
    private static final long HOUR = 100;
    private static final long MINUTE= 400;
    //End declaration of the time

    //Start declaration of the objects
    private GoogleApiClient apiClient;
    private ImageButton imageButton;
    //End declaration of the objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setupGoogleApiClient();
       /* this.imageButton = (ImageButton) findViewById(R.id.vibrateButton);
        this.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(MainActivity.this.VIBRATOR_SERVICE);
                MakePatternToVibrate toVibrate = new MakePatternToVibrate();
                vibrator.vibrate(toVibrate.setupPattern(), -1);
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( this.apiClient != null && !( this.apiClient.isConnected() || this.apiClient.isConnecting() ) )
            this.apiClient.connect();
    }

    @Override
    protected void onStop() {
        if ( this.apiClient != null ) {
            Wearable.MessageApi.removeListener( this.apiClient, this );
            if ( this.apiClient.isConnected() ) {
                this.apiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if( this.apiClient != null )
            this.apiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( this.apiClient, this );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived( final MessageEvent messageEvent ) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
                    final String message = String.valueOf(messageEvent.getData());
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupGoogleApiClient() {
        // Setup googleApiClient and it try to connection
        this.apiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();

        this.apiClient.connect();
    }
}
