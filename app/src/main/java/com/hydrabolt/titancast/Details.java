package com.hydrabolt.titancast;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Amish on 01/05/2015.
 */
public class Details {

    private static final String APP_VERSION = BuildConfig.VERSION_NAME;
    private static boolean connected = false, hasViewData = false;
    private static Activity activity;
    private static boolean shownUpdate = false;

    public static boolean hasViewData() {
        return hasViewData;
    }
    public static void setHasViewData(boolean a){
        hasViewData = a;
    }

    public static String getAppVersion(){
        return APP_VERSION;
    }

    public static void setContext(Activity a) {
        activity = a;
    }

    public static void setShownUpdate(boolean shownUpdate) {
        Details.shownUpdate = shownUpdate;
    }

    public static boolean getShownUpdate() {
        return shownUpdate;
    }


    public static void showUpdate(String v, boolean override) {

        if(!UpdateActivity.open) {
            Intent intent = new Intent(activity.getApplicationContext(), UpdateActivity.class);
            intent.putExtra("version", v);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.startActivity(intent);
            return;
        }

    }

    public static void setConnected(boolean connected) {
        Details.connected = connected;
    }
    public static boolean connected(){
        return connected;
    }
}
