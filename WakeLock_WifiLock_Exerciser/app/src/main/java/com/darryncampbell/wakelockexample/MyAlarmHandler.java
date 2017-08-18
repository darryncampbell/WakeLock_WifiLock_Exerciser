package com.darryncampbell.wakelockexample;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyAlarmHandler extends IntentService {

    public MyAlarmHandler() {
        super("MyAlarmHandler");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent responseIntent = new Intent();
            responseIntent.putExtra("source", "alarm");
            responseIntent.setAction(MainActivity.ResponseReceiver.LOCAL_ACTION);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(responseIntent);
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
            String theTime = df.format(Calendar.getInstance().getTime());
            Log.i("Wake Lock Test (Serv)", "Alarm Fired: " + theTime);

            setAlarm();
        }
    }

    void setAlarm()
    {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
        int TWO_SECONDS = 2000;
        long fireDelay = new Date().getTime() + TWO_SECONDS;
        Intent alarmFiredIntent = new Intent(MyAlarmHandler.this, MyAlarmHandler.class);
        PendingIntent piAlarmFiredIntent = PendingIntent.getService(this, 0, alarmFiredIntent, 0);
        am.setExact(ALARM_TYPE, fireDelay, piAlarmFiredIntent);
    }


}
