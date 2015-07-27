package com.hydrabolt.titancast;

import android.util.Log;
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

        if (alreadyShown) {
            if (!override) {
                return;
            }
        }

        alreadyShown = true;

        String myVersion = Details.getAppVersion();

        try {

            InputStream in = new URL("http://192.168.0.9:4000/download/getlatest" + Details.getDownloadChannel() + ".html").openStream();

            try {
                String lines[] = IOUtils.toString(in).split("\\r?\\n");

                String version = lines[0];
                int intVersion = Integer.parseInt(lines[1]);
                String downloadURL = lines[2];
                String buildType = lines[3];

                if (intVersion > Details.getAppVersionInt()) {

                    Details.showUpdate(version, intVersion, downloadURL, buildType, override);
                    return;

                } else {

                    if (override) {
                        //only notify the user that no updates were found if they manually requested for an update check
                        TitanCastNotification.showToast("No updates found", Toast.LENGTH_LONG);
                    }

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
