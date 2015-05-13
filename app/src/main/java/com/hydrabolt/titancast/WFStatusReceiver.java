package com.hydrabolt.titancast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Amish on 01/05/2015.
 */
public class WFStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo != null) {
            if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

                MainActivity.wifiStateChanged(2, wm.getConnectionInfo().getIpAddress());
            }
        } else {
            MainActivity.wifiStateChanged(0, -1);
        }

    }
}
