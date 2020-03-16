# WakeLock_WifiLock_Exerciser
Small application to test functionality of WifiLock and WakeLock on Android

##  EMDK Dependency

Application **will** run on both Zebra and non-Zebra Android devices.  When running on Zebra devices the application will take advantage of MX to enable or disable battery optimizations via buttons on the UI.  This does require:
* EMDK to be installed on your build machine (https://www.zebra.com/us/en/support-downloads/software/developer-tools/emdk-for-android.html) **Version 6.7 or higher** (though I had it working with EMDK 6.6 in testing)
* A device that supports MX7.x which as the numbering implies will be at least Nougat.  You can check the MX level from Settings --> About --> SW components --> MX (MXMF version)
* A device whose MX supports BatteryOptimization.  Some features of MX are not available on Zebra's value tier devices (e.g. TC2x).  Please check http://techdocs.zebra.com/mx/ for compatibility.

_This only affects the bottom two buttons on the UI which refer to controlling BatteryOptimizations via MX_ 

## Repository includes a small python 3 server to receive posts from the application

    python dummy-web-server.py
    
## Determine current wake / wifi locks in effect as follows:

    adb shell dumpsys power
    adb shell dumpsys wifi

## Optionally, force the device power state to idle as follows:

    adb shell dumpsys battery unplug
    adb shell dumpsys deviceidle force-idle
    (verify with) adb shell dumpsys deviceidle (& observe mState)

## Notes

- Uncomment the annotated line in onCreate() to create a separate wake lock on launch
- Tested on TC51
- Would never pass PlayStore criteria as includes `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
  - Update (16/Mar/20): This app got removed from the Play Store for "Violation of the Broken Functionality policy".  This was possibly because the older version in the Play Store had an issue checking for the EMDK on non-Zebra devices running Android 10 but it's not clear - it could also be because I requested this action). 
- Make sure nothing else on your device holds wake locks before running tests
- Zebra devices also have additional settings to keep wifi on during standby (under advanced WiFi settings)
