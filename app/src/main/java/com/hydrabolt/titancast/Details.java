package com.hydrabolt.titancast;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Amish on 01/05/2015.
 */
public class Details {

    public static String APP_VERSION = "0.0.1";

    public static boolean connected = false, haveViewData = false;
    public static Activity a;
    public static boolean shownUpdate = false;

    public static void setContext(Activity ac){
        a = ac;
    }

    public static void showUpdate(String v) {

        if(shownUpdate) {
            return;
        }

        shownUpdate = true;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://titancast.github.io/updates/updater.html?v="+APP_VERSION));
        PendingIntent pIntent = PendingIntent.getActivity(a.getApplicationContext(), 0, intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(a.getApplicationContext())
                .setContentTitle("Update Available")
                .setContentText("An update is available - tap to install")
                .setSmallIcon(R.drawable.notificon)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setTicker("An update to TitanCast is available")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).build();


        NotificationManager notificationManager =
                (NotificationManager) a.getApplicationContext().getSystemService(a.getApplicationContext().NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(a.getApplicationContext(), "Update available - check notifications!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
