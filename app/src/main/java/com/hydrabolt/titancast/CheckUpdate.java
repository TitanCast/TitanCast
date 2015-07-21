package com.hydrabolt.titancast;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Amish on 03/05/2015.
 */
public class CheckUpdate implements Runnable {

    boolean override = false, alreadyShown = false;

    public CheckUpdate(boolean override) {
        this.override = override;
    }


    @Override
    public void run() {

        if(alreadyShown){
            if(!override){
                return;
            }
        }

        alreadyShown = true;

        String myVersion = Details.getAppVersion();

        try {

            InputStream in = new URL("http://titancast.github.io/download/getlatest.html").openStream();

            try {
                String lines[] = IOUtils.toString(in).split("\\r?\\n");

                if(lines[0].equals( Details.getAppVersion() )) {

                    if (override) {
                        //only notify the user that no updates were found if they manually requested for an update check
                        TitanCastNotification.showToast("No updates found", Toast.LENGTH_LONG);
                    }

                }else{

                    Details.showUpdate(lines[0], override);
                    return;

                }

            } catch (Exception e) {
                if (override) {
                    TitanCastNotification.showToast("Error fetching update list", Toast.LENGTH_LONG);
                    Log.d("titancast", e.getLocalizedMessage());
                }
            } finally {
                IOUtils.closeQuietly(in);
            }

        } catch (IOException e) {
            //nope
            TitanCastNotification.showToast("Error fetching update list", Toast.LENGTH_LONG);
        }

    }
}
