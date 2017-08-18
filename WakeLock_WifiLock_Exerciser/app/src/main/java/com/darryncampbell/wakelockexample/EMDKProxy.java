package com.darryncampbell.wakelockexample;

/**
 * Created by darry on 18/08/2017.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * The EMDK Proxy class is responsible for determining whether the EMDK is installed on the
 * target device and failing gracefully if it is not installed.
 */
public class EMDKProxy {

    private EMDKInterface emdk = null;
    private Context context;
    public static final String LOG_TAG = "Wake Lock Test";

    public EMDKProxy(Context c)
    {
        this.context = c;
        if (isEMDKAvailable(c))
        {
            emdk = new EMDKInterface(c);
        }
    }

    public boolean isEMDKAvailable(Context c)
    {
        try
        {
            EMDKInterface test = new EMDKInterface(c);
            Log.i(LOG_TAG, "EMDK is available on this device");
            return true;
        }
        catch (NoClassDefFoundError e)
        {
            Log.w(LOG_TAG, "EMDK is not available on this device");
            return false;
        }
    }

    public boolean setBatteryOptimizations(Boolean bEnabled)
    {
        if (emdk == null)
        {
            Log.w(LOG_TAG, "Attempted to set Battery Optimizations but EMDK is not available");
            Toast.makeText(context, "Functionality only supported on Zebra devices running MX 7+", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return emdk.setBatteryOptimizations(bEnabled);
        }
    }

    public void Close()
    {
        if (emdk != null)
            emdk.Destroy();
    }
}
