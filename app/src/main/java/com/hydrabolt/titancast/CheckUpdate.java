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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Amish on 03/05/2015.
 */
public class CheckUpdate implements Runnable {

    Activity a;
    boolean override = false;

    public CheckUpdate(Activity a, boolean override){
        this.a = a;
        this.override = override;
    }


    @Override
    public void run() {

        String myVersion = Details.APP_VERSION;

        try {

            InputStream in = new URL("http://titancast.github.io/updates/version.history").openStream();

            try {
                String lines[] = IOUtils.toString(in).split("\\r?\\n");

                for(String line : lines){

                    if(!line.startsWith("#")){
                        String[] details = line.split("=");

                        if(!details[0].equals(myVersion)){
                            Details.showUpdate(details[0]);
                        }else{
                            if(override) {
                                a.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(a.getApplicationContext(), "No updates found", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            return;

                        }
                    }

                }

            } catch (Exception e){

            } finally {
                IOUtils.closeQuietly(in);
            }

        } catch (IOException e) {
            //nope
            Log.d("a", e.getLocalizedMessage());
        }

    }
}
