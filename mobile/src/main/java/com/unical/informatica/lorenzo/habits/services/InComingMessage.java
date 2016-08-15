package com.unical.informatica.lorenzo.habits.services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
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
 * Created by Lorenzo on 08/08/2016.
 */
public class InComingMessage extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";
    private static final String LOGFILE = "log";
    private String day;
    private int time;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent recieved: " + intent.getAction());
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    String record = this.buildRecord(this.getContactName(context, messages[0].getOriginatingAddress()));
                    BuildFile.getInstance(context,LOGFILE).appendFileValue(LOGFILE, record , context);
                }
            }
        }
    }

    private void getTime() {

        final Calendar mCalendar = Calendar.getInstance();
        final Date mDate = new Date();
        mCalendar.setTime(mDate);
        day = new SimpleDateFormat("E").format(mDate) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH);
        time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    private String buildRecord(String user) {
        this.getTime();
        return new StringBuilder().buildTuple("?", new Day(day).getDayOfWeek(), new Time(time).getTime(), "message", user, "?", "?");
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