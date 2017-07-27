package com.darryncampbell.wakelockexample;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
        }
    }

}
