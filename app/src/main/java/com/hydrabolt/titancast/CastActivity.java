package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hydrabolt.titancast.sensors.AccelerometerSensor;
import com.hydrabolt.titancast.sensors.TCSensorManager;


public class CastActivity extends AppCompatActivity {

    public static Handler h;
    static WebView castView;
    static Activity activity;
    private static TCSensorManager sensorManager;
    private static Sensor sensorAccelerometer;
    private static AccelerometerSensor accSensor;

    public static Activity getActivity() {
        return activity;
    }

    public static TCSensorManager getSensorManager() {
        return sensorManager;
    }

    public static void sendCustom(String dat) {
        castView.loadUrl("javascript:customDataReceived('" + dat + "')");
    }

    public static void close() {
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MainActivity.getServer().setCastActivity(this);

        if (!Details.connected()) {
            finish();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cast);

        getSupportActionBar().hide();

        goImmersive();

        h = new Handler();

        Intent i = getIntent();
        String url = i.getStringExtra("url");

        View v = findViewById(R.id.castWebView);

        castView = (WebView) findViewById(R.id.castWebView);
        castView.clearCache(true);
        WebSettings castViewSettings = castView.getSettings();
        castViewSettings.setJavaScriptEnabled(true);
        castView.setWebViewClient(new WebViewClient(){

            private boolean finished = false;

            @Override
            public void onPageFinished(WebView view, String url) {

                if(!finished){
                    finished = true;
                    String[] empty = {};
                    MainActivity.getServer().sendPacketToActive(PacketSerializer.generatePacket("view-loaded", empty));
                }

            }

        });
        castView.addJavascriptInterface(new JSBridge(getApplicationContext(), this), "device");
        castView.loadUrl(url);

        activity = this;

        sensorManager = new TCSensorManager(this);
        sensorManager.disableAccelerometerSensor(); // default
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            goImmersive();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.getServer().terminateActive();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerAll();
    }

    public void goImmersive() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
