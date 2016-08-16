package com.unical.informatica.lorenzo.habits.services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.unical.informatica.lorenzo.habits.model.Day;
import com.unical.informatica.lorenzo.habits.model.Time;
import com.unical.informatica.lorenzo.habits.support.BuildFile;
import com.unical.informatica.lorenzo.habits.support.StringBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * handler to register a new tuple when arrive a phone call
 */
public class InComingCall extends BroadcastReceiver {

    private static final String LOGFILE = "log";
    private String day;
    private int time;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // TELEPHONY MANAGER class object to register one listner
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        //Create Listner
        MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

        // Register listener for LISTEN_CALL_STATE
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        private boolean callChanged = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            Log.i("MyPhoneListener", state + "   incoming no:" + incomingNumber);
            /**
             *  state = 0 means when user close a callphone
             */
            if (state == 0) {
                String record = this.buildRecord(this.getContactName(context, incomingNumber));
                if(record != "null" && !record.equals(incomingNumber)) {
                    new BuildFile(InComingCall.this.context, new Day(day).getDayOfWeek());
                    BuildFile.getInstance(InComingCall.this.context, LOGFILE).appendFileValue(LOGFILE, record, InComingCall.this.context);
                }

            }
        }

        private void getTime() {
            Date mDate = new Date();
            final Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTime(mDate);
            day = new SimpleDateFormat("E").format(mDate) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH);
            time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }

        private String buildRecord(String user) {
            this.getTime();
            return new StringBuilder().buildTuple("?", new Day(day).getDayOfWeek(), new Time(time).getTime(), "call", user, "?", "?");
        }

        private String getContactName(Context context, String phoneNumber) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor == null) {
                return null;
            }
            String contactName = null;
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            if (contactName != null)
                return contactName;
            else
                return phoneNumber;
        }
    }

}