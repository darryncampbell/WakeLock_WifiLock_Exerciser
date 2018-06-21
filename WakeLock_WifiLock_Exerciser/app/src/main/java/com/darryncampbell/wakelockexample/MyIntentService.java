package com.darryncampbell.wakelockexample;

import android.app.IntentService;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static com.darryncampbell.wakelockexample.MainActivity.LOG_TAG;

public class MyIntentService extends IntentService {

    public static final String REQUEST_POST_ADDRESS = "server";
    public static final String REQUEST_COUNT = "count";
    public static final String REQUEST_ACTION_BEEP = "beep";
    public static final String REQUEST_ACTION_POST = "post";
    public static final String REQUEST_TIMEOUT = "timeout";
    public static volatile boolean shouldContinue = true;

    public MyIntentService() {
        super("MyIntentService");
        //  Set to true to have IntentService START_REDELIVER_INTENT (https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/app/IntentService.java#99)
        //  default is false
        setIntentRedelivery(false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String requestString = intent.getStringExtra(REQUEST_POST_ADDRESS);
            long count = intent.getLongExtra(REQUEST_COUNT, 0);
            boolean requestBeep = intent.getBooleanExtra(REQUEST_ACTION_BEEP, true);
            boolean requestPost = intent.getBooleanExtra(REQUEST_ACTION_POST, true);
            int timeout = intent.getIntExtra(REQUEST_TIMEOUT, 1000);
            try
            {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 20);
                for(;;) {
                    if (requestPost)
                    {
                        count++;
                        Log.i(LOG_TAG, "Posting Data " + count);
                        String message = "";
                        if (android.os.Build.VERSION.SDK_INT >= 28) {
                            //  For Android P, output the current app standby bucket
                            UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
                            int appStandbyBucket = usageStatsManager.getAppStandbyBucket();
                            if (appStandbyBucket <= UsageStatsManager.STANDBY_BUCKET_ACTIVE) {
                                message = "AppStandbyBucket_is_ACTIVE";
                            }
                            else if (appStandbyBucket <= UsageStatsManager.STANDBY_BUCKET_WORKING_SET) {
                                message = "AppStandbyBucket_is_WORKING_SET";
                            }
                            else if (appStandbyBucket <= UsageStatsManager.STANDBY_BUCKET_FREQUENT) {
                                message = "AppStandbyBucket_is_FREQUENT";
                            }
                            else if (appStandbyBucket <= UsageStatsManager.STANDBY_BUCKET_RARE) {
                                message = "AppStandbyBucket_is_RARE";
                            }
                            else {
                                message = "AppStandbyBucket_is_unrecognised";
                            }
                        }

                        postData(requestString, count, message);
                    }
                    if (requestBeep)
                    {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    }
                    //Thread.sleep(20000);
                    Thread.sleep(timeout);
                    if (!shouldContinue)
                    {
                        //  Notify main activity that we have finished
                        Intent responseIntent = new Intent();
                        responseIntent.setAction(MainActivity.ResponseReceiver.LOCAL_ACTION);
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                        localBroadcastManager.sendBroadcast(responseIntent);
                        break;

                    }
                }
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void postData(String server, long count, String message) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(server);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("id", count + ""));
            nameValuePairs.add(new BasicNameValuePair("message", message));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }
}
