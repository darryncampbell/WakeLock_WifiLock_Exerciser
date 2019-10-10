package com.darryncampbell.wakelockexample;

/**
 * Created by darry on 25/04/2017.
 */

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import fi.iki.elonen.NanoHTTPD;

public class MyHTTPServer extends NanoHTTPD {

    private Context context;

    public MyHTTPServer(int port, Context context) throws IOException {
        super(port);
        this.context = context;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void stopServer()
    {
        stop();
    }

    @Override
    public Response serve(IHTTPSession session) {

        String testMessage = "";
        if (session.getUri().equals("/test_service_start"))
        {
            //  Do some service test
            testMessage = "Starting Service";
            MyIntentService.shouldContinue = true;
            Intent msgIntent = new Intent(context, MyIntentService.class);
            msgIntent.putExtra(MyIntentService.REQUEST_POST_ADDRESS, "http://10.0.2.15:8082");
            msgIntent.putExtra(MyIntentService.REQUEST_ACTION_BEEP, false);
            msgIntent.putExtra(MyIntentService.REQUEST_ACTION_POST, true);
            msgIntent.putExtra(MyIntentService.REQUEST_TIMEOUT, 10000);
            context.startService(msgIntent);
        }
        else if (session.getUri().equals("/test_service_stop"))
        {
            testMessage = "Stopping Service - please wait about 30 seconds (there will be no feedback)";
            MyIntentService.shouldContinue = false;
        }
        String msg = "<html><body><h1>Hello from Wake lock / Wifi lock test app</h1>\n";
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
        String theTime = df.format(Calendar.getInstance().getTime());
        msg += "<P>The time is: " + theTime;
        msg += "<P>" + testMessage;
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
