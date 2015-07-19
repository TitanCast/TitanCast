package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Amish on 01/05/2015.
 */
public class JSBridge {

    Context ctx;
    Vibrator vibrator;
    Activity activity;

    JSBridge(Context c, Activity a) {
        ctx = c;
        activity = a;
        vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @JavascriptInterface
    public void sendData(String data) {

        try {

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            String[] finalArray = new String[ jsonArray.length() ];

            for(int i = 0; i < jsonArray.length(); i++){
                finalArray[i] = jsonArray.getString(i);
            }

            WSServer.sendCustomDataToActive( finalArray );

        } catch (JSONException exception) {

            Log.d("titancast-jsbridge", "error parsing JSON - " + exception.getLocalizedMessage());
            return;

        }
    }

    @JavascriptInterface
    public void vibrate(long d) {
        vibrator.vibrate(d);
    }

    @JavascriptInterface
    public void setOrientation(String orientation) {
        if (orientation.equals("portrait")) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation.equals("landscape")) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation.equals("reverse_portrait")) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        } else if (orientation.equals("reverse_landscape")) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    @JavascriptInterface
    public String getOrientation(String s) {
        String orientation = "error";
        switch (Integer.parseInt(s)) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                orientation = "landscape";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                orientation = "landscape";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE:
                orientation = "landscape";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                orientation = "reverse_landscape";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                orientation = "portrait";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                orientation = "portrait";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT:
                orientation = "portrait";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                orientation = "reverse_portrait";
                break;
            default:
                orientation = "unknown";
                break;
        }
        return orientation;
    }

}
