# WakeLock_WifiLock_Exerciser
Small application to test functionality of WifiLock and WakeLock on Android

##  EMDK Dependency

Application **will** run on both Zebra and non-Zebra Android devices.  When running on Zebra devices the application will take advantage of MX to enable or disable battery optimizations via buttons on the UI.  This does require EMDK to be installed on your build machine (https://www.zebra.com/us/en/support-downloads/software/developer-tools/emdk-for-android.html) **Version 6.6.x or higher** 

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
- Make sure nothing else on your device holds wake locks before running tests
- Zebra devices also have additional settings to keep wifi on during standby (under advanced WiFi settings)
