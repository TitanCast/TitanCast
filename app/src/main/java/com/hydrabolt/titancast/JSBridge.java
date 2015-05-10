package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.util.Base64;
import android.webkit.JavascriptInterface;

/**
 * Created by Amish on 01/05/2015.
 */
public class JSBridge{

    Context ctx;
    Vibrator vibrator;
    Activity activity;

    JSBridge(Context c, Activity a){
        ctx = c;
        activity = a;
        vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @JavascriptInterface
    public void sendData(String data){
        WSServer.sendToActive(Base64.encodeToString(data.getBytes(), Base64.DEFAULT));
    }

    @JavascriptInterface
    public void vibrate(long d){
        vibrator.vibrate(d);
    }

    @JavascriptInterface
    public void setOrientation(String orientation){
        if(orientation.equals("portrait")) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if(orientation.equals("landscape")){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else if(orientation.equals("reverse_portrait")){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }else if(orientation.equals("reverse_landscape")){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    @JavascriptInterface
    public String getOrientation(String s){
        String orientation = "error";
        switch(Integer.parseInt(s)){
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
