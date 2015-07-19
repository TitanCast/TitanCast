package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import java.io.File;
import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {

    public static Activity activity;
    private static TextView statusSubtitle, status;
    private static WSServer server;
    private static boolean connected = false;
    private IntentFilter intentFilter;
    private WFStatusReceiver wFR;

    public static void wifiStateChanged(int state, int ip) {

        connected = (state == 2);
        status.setTypeface(Typeface.DEFAULT);

        if (state == 2) {
            statusSubtitle.setText("connect to");
            status.setText(FormattingTools.getIP(ip));
            status.setTypeface(Typeface.MONOSPACE);

        } else {
            statusSubtitle.setText("uh-oh");
            status.setText("connect to wi-fi");
        }
    }

    public static void checkWifiStatus() {
        Context ctx = activity.getApplicationContext();

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        WifiManager wm = (WifiManager) ctx.getSystemService(WIFI_SERVICE);

        if (ni.isConnected()) {

            int ip = wm.getConnectionInfo().getIpAddress();
            wifiStateChanged(2, ip);

        } else {
            wifiStateChanged(0, -1);
        }
    }

    public static WSServer getServer() {
        return server;
    }

    public static void checkForUpdate(boolean override) {
        new Thread(new CheckUpdate(override)).start();
    }

    private void registerViews() {
        statusSubtitle = (TextView) findViewById(R.id.statusSubtitle);
        status = (TextView) findViewById(R.id.status);
    }

    private void setupViews() {
        statusSubtitle.setText("just a second");
        status.setText("please wait");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        registerViews();
        setupViews();

        TitanCastNotification.setActivity(activity);

        checkWifiStatus();

        server = new WSServer(new InetSocketAddress(25517), getApplicationContext());
        server.start();

        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(100);

        wFR = new WFStatusReceiver();

        registerReceiver(wFR, intentFilter);

        Details.setContext(this);

        Log.d("TITANCAST", "uPDTE FORENGOERWNGEWURI45IYERIUT");
        checkForUpdate(false);

        File f = new File(activity.getExternalCacheDir() + "titancast.apk");

        if(f.exists()){
            f.delete();
            Log.d("titancast", "deleted previous app update file");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wFR, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wFR);
        server.end();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.check_update) {
            checkForUpdate(true);
            return true;
        }
        if (id == R.id.menu_website) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://titancast.github.io/"));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
