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
        modifyData[0] = giveProfileForBatteryOptimizations(!bEnabled, context.getPackageName());
        EMDKResults results = profileManager.processProfile("BatteryOptimizationProfile",
                ProfileManager.PROFILE_FLAG.SET, modifyData);

        if (results.statusCode == EMDKResults.STATUS_CODE.CHECK_XML)
        {
            //  todo did this work??
        }
        else
        {
            Toast.makeText(context, "Failed to change battery optimizations.  Do you have MX7+ and running M or above?", Toast.LENGTH_LONG).show();
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

    private String giveProfileForBatteryOptimizations(Boolean bDisable, String packageName)
    {
        if (bDisable)
        {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<wap-provisioningdoc>\n" +
                    "  <characteristic type=\"ProfileInfo\">\n" +
                    "    <parm name=\"created_wizard_version\" value=\"6.6.0\"/>\n" +
                    "  </characteristic>\n" +
                    "  <characteristic type=\"Profile\">\n" +
                    "    <parm name=\"ProfileName\" value=\"BatteryOptimizationProfile\"/>\n" +
                    "    <parm name=\"ModifiedDate\" value=\"2017-08-18 09:07:59\"/>\n" +
                    "    <parm name=\"TargetSystemVersion\" value=\"7.0\"/>\n" +
                    "    <characteristic type=\"AppMgr\" version=\"7.0\">\n" +
                    "      <parm name=\"emdk_name\" value=\"AppManagerBatteryOptimizations\"/>\n" +
                    "      <parm name=\"Action\" value=\"BatteryOptimization\"/>\n" +
                    "      <parm name=\"AddPackageNames\" value=" + packageName + "/>\n" +
                    "    </characteristic>\n" +
                    "  </characteristic>\n" +
                    "</wap-provisioningdoc>\n";
        }
        else
        {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<wap-provisioningdoc>\n" +
                    "  <characteristic type=\"ProfileInfo\">\n" +
                    "    <parm name=\"created_wizard_version\" value=\"6.6.0\"/>\n" +
                    "  </characteristic>\n" +
                    "  <characteristic type=\"Profile\">\n" +
                    "    <parm name=\"ProfileName\" value=\"BatteryOptimizationProfile\"/>\n" +
                    "    <parm name=\"ModifiedDate\" value=\"2017-08-18 09:07:59\"/>\n" +
                    "    <parm name=\"TargetSystemVersion\" value=\"7.0\"/>\n" +
                    "    <characteristic type=\"AppMgr\" version=\"7.0\">\n" +
                    "      <parm name=\"emdk_name\" value=\"AppManagerBatteryOptimizations\"/>\n" +
                    "      <parm name=\"Action\" value=\"BatteryOptimization\"/>\n" +
                    "      <parm name=\"RemovePackageNames\" value=" + packageName + "/>\n" +
                    "    </characteristic>\n" +
                    "  </characteristic>\n" +
                    "</wap-provisioningdoc>\n";
        }
    }

}
