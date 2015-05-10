package com.hydrabolt.titancast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Amish on 01/05/2015.
 */
public class WFStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo nInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        if(nInfo != null){
            if(nInfo.isConnected()){

                int ip = wm.getConnectionInfo().getIpAddress();
                MainActivity.wifiStateChanged(2, ip);

            }else if(nInfo.isConnectedOrConnecting()){

                MainActivity.wifiStateChanged(1, -1);
            }else{
                MainActivity.wifiStateChanged(0, -1);
            }
        }else {
            MainActivity.wifiStateChanged(0, -1);
        }
    }
}
