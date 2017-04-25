package com.darryncampbell.wakelockexample;

/**
 * Created by darry on 25/04/2017.
 */

import android.text.format.Time;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import fi.iki.elonen.NanoHTTPD;

import static com.darryncampbell.wakelockexample.MainActivity.LOG_TAG;

public class MyHTTPServer extends NanoHTTPD {
    public MyHTTPServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void stopServer()
    {
        stop();
    }

    @Override
    public Response serve(IHTTPSession session) {

        String msg = "<html><body><h1>Hello from Wake lock / Wifi lock test app</h1>\n";
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
        String theTime = df.format(Calendar.getInstance().getTime());
        msg += "<P>The time is: " + theTime;
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
