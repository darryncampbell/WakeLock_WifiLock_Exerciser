package com.darryncampbell.wakelockexample;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

/**
 * Created by darry on 18/08/2017.
 */

public class EMDKInterface implements EMDKManager.EMDKListener {

    public static final String LOG_TAG = "Wake Lock Test";
    private EMDKManager emdkManager;
    private ProfileManager profileManager;
    private Context context;

    public EMDKInterface(Context c)
    {
        this.context = c;
        EMDKResults results = EMDKManager.getEMDKManager(c, this);
        if (results.statusCode == EMDKResults.STATUS_CODE.SUCCESS)
        {
            Log.i(LOG_TAG, "EMDK Manager has been successfully created");
        }
        else
        {
            Log.w(LOG_TAG, "Some error has occurred creating the EMDK manager.  EMDK functionality will not be avialable");
        }
    }

    public void Destroy() {
        if (emdkManager != null)
        {
            emdkManager.release(EMDKManager.FEATURE_TYPE.PROFILE);
            emdkManager.release();
        }
    }

    public boolean setBatteryOptimizations(Boolean bEnabled) {
        //  Apply the battery optimization profile
        String[] modifyData = new String[1];
        EMDKResults results = null;
        if (profileManager == null)
            return false; //  EMDK initialisation error can cause this

        if (bEnabled)
        {
            //  Remove from whitelist / turn on optimization
            results = profileManager.processProfile("BatteryOptimizationProfileAddApps",
                    ProfileManager.PROFILE_FLAG.SET, modifyData);
        }
        else
        {
            //  Add to whitelist / turn off optimization
            results = profileManager.processProfile("BatteryOptimizationProfileRemoveApps",
                    ProfileManager.PROFILE_FLAG.SET, modifyData);
        }

        if (results.statusCode == EMDKResults.STATUS_CODE.SUCCESS)
        {
            Toast.makeText(context, "Successfuly updated battery optimizations whitelist", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (results.statusCode == EMDKResults.STATUS_CODE.CHECK_XML)
        {
            if (results.getStatusString().contains("characteristic-error"))
            {
                //  Setting has failed to be applied
                Log.e(LOG_TAG, results.getStatusString() + "-" + results.toString());
                Toast.makeText(context, context.getResources().getString(R.string.message_battery_optimization_error), Toast.LENGTH_LONG).show();
                return false;
            }
            else
            {
                //  Setting was successfully applied
                Toast.makeText(context, "Successfuly updated battery optimizations whitelist", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        else
        {
            Toast.makeText(context, context.getResources().getString(R.string.message_battery_optimization_error), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        this.profileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);
        Log.i(LOG_TAG, "EMDK has opened and will now be ready");
    }

    @Override
    public void onClosed() {
        Log.i(LOG_TAG, "EMDK has closed and is no longer ready");
        this.emdkManager = null;
        this.profileManager = null;
    }

}
