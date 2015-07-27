package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Intent;

import com.hydrabolt.titancast.util.DownloadChannel;

/**
 * Created by Amish on 01/05/2015.
 */
public class Details {

    private static final String APP_VERSION = BuildConfig.VERSION_NAME;
    private static final int INT_APP_VERSION = BuildConfig.VERSION_CODE;

    private static String downloadChannel = DownloadChannel.STABLE;

    private static boolean connected = false, hasViewData = false;
    private static Activity activity;
    private static boolean shownUpdate = false;

    public static boolean hasViewData() {
        return hasViewData;
    }

    public static void setHasViewData(boolean a) {
        hasViewData = a;
    }

    public static String getAppVersion() {
        return APP_VERSION;
    }

    public static int getAppVersionInt() {
        return INT_APP_VERSION;
    }

    public static void setContext(Activity a) {
        activity = a;
    }

    public static boolean getShownUpdate() {
        return shownUpdate;
    }

    public static void setShownUpdate(boolean shownUpdate) {
        Details.shownUpdate = shownUpdate;
    }

    public static void showUpdate(String v, int intVersion, String download, String buildType, boolean override) {

        if (!UpdateActivity.open) {
            Intent intent = new Intent(activity.getApplicationContext(), UpdateActivity.class);
            intent.putExtra("version", v);
            intent.putExtra("intVersion", intVersion);
            intent.putExtra("download", download);
            intent.putExtra("buildType", buildType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.startActivity(intent);
            return;
        }

    }

    public static String getDownloadChannel(){
        return downloadChannel;
    }

    public static void setConnected(boolean connected) {
        Details.connected = connected;
    }

    public static boolean connected() {
        return connected;
    }
}
