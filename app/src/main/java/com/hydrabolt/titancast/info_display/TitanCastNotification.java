package com.hydrabolt.titancast.info_display;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Amish on 10/05/2015.
 */
public class TitanCastNotification {

    private static Activity activity;

    public static void setActivity(Activity a) {
        activity = a;
    }

    public static void display(Activity activity, int id, Notification n){
        NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.notify(id, n);
    }

    public static Notification build(String title, String text, int icon, PendingIntent intent, boolean autoCancel, String ticker, Uri sound){
        Notification n = new Notification.Builder(activity.getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setContentIntent(intent)
                .setAutoCancel(autoCancel)
                .setTicker(ticker)
                .setSound(sound).build();
        return n;
    }

    public static void showToast(final String text, final int length){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), text, length).show();
            }
        });
    }

}
