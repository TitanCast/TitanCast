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

    static WebView castView;
    static Activity activity;
    public static Handler h;

    private static TCSensorManager sensorManager;
    private static Sensor sensorAccelerometer;
    private static AccelerometerSensor accSensor;

    public static Activity getActivity() {
        return activity;
    }

    public static TCSensorManager getSensorManager() {
        return sensorManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(!Details.connected()){
            finish();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cast);

        getSupportActionBar().hide();

        h = new Handler();

        Intent i = getIntent();
        String url = i.getStringExtra("url");

        View v = findViewById(R.id.castWebView);

        castView = (WebView) findViewById(R.id.castWebView);
        WebSettings castViewSettings = castView.getSettings();
        castViewSettings.setJavaScriptEnabled(true);
        castView.setWebViewClient(new WebViewClient());
        castView.addJavascriptInterface(new JSBridge(this, this), "device");
        castView.loadUrl(url);

        activity = this;

        sensorManager = new TCSensorManager(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        MainActivity.getServer().terminateActive();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public static void sendCustom(String dat){
        castView.loadUrl("javascript:customDataReceived('" + dat + "')");
    }

    public static void close(){
        activity.finish();
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
}
