package com.hydrabolt.titancast;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

/**
 * Created by Amish on 01/05/2015.
 */
public class Details {

    private static final String APP_VERSION = "0.0.1";
    private static boolean connected = false, haveViewData = false;
    private static Activity activity;
    private static boolean shownUpdate = false;

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

        if (shownUpdate && !override) {
            return;
        }

        shownUpdate = true;

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://titancast.github.io/updates/updater.html?v=" + APP_VERSION));
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

        TitanCastNotification.showToast("Update available - check notification drawer", Toast.LENGTH_LONG);
    }

}
