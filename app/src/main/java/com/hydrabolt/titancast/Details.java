package com.hydrabolt.titancast;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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

    private static final String APP_VERSION = "0.0.1";
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

        Intent intent = new Intent(activity.getApplicationContext(), UpdateActivity.class);
        intent.putExtra("version", v);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);
        return;

        /*File file;
        try {
            file = new File(activity.getExternalCacheDir() + "titancast.apk");

            FileUtils.copyURLToFile(new URL("http://titancast.github.io/download/v/titancast.0.0.2.apk"), file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType( Uri.fromFile(file) , "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (shownUpdate && !override) {
            return;
        }

        shownUpdate = true;

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://titancast.github.io/download/update.html?v=" + APP_VERSION));
        PendingIntent pIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        Notification n = TitanCastNotification.build(
                "Update Available",
                "An update is available - tap to install",
                R.drawable.notificon,
                pIntent,
                true,
                "An update to TitanCast is available",
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        );
        TitanCastNotification.display(activity, 0, n);

        TitanCastNotification.showToast("Update available - check notification drawer", Toast.LENGTH_LONG);*/
    }

    public static void setConnected(boolean connected) {
        Details.connected = connected;
    }
    public static boolean connected(){
        return connected;
    }
}
