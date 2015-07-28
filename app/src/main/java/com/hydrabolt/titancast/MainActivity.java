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
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends Activity {

    public static Activity activity;
    public static DefaultSSLWebSocketServerFactory wsf;
    private static TextView statusSubtitle, status;
    private static WSServer server;
    private static boolean connected = false;
    private static boolean checkedUpdate = false;
    private static File extCacheDir;
    private IntentFilter intentFilter;
    private WFStatusReceiver wFR;

    public static void wifiStateChanged(int state, int ip) {

        connected = (state == 2);
        status.setTypeface(Typeface.DEFAULT);

        if (state == 2) {
            statusSubtitle.setText("ready");
            status.setText(FormattingTools.getIP(ip));
            status.setTypeface(Typeface.MONOSPACE);

        } else {
            statusSubtitle.setText("not ready");
            status.setText("connect to wi-fi");
        }
    }

    static boolean alreadyOpenedServer = false;

    public void tryOpeningServer() {

        if(alreadyOpenedServer){
            return;
        }

        server = new WSServer(new InetSocketAddress(25517), activity.getApplicationContext());

        alreadyOpenedServer = true;

        String STOREPASSWORD = "titancast-androidapp";
        String KEYPASSWORD = STOREPASSWORD;

        try {
            KeyStore ks = KeyStore.getInstance("BKS");
            ks.load(getAssets().open("keystore.jks"), STOREPASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(ks, KEYPASSWORD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ks);

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            wsf = new DefaultSSLWebSocketServerFactory(sslContext);

            server.setWebSocketFactory(wsf);

            server.start();

        } catch (Exception e) {
            e.printStackTrace();
            //error
            fatalError(e);
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

        TextView version = (TextView) findViewById(R.id.versionText);

        String extra = BuildConfig.DEBUG ? "\nDEVELOPER EDITION" : "";

        version.setText("v" + Details.getAppVersion() + "\nBuild "+ Details.getAppVersionInt() + extra);
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
        extCacheDir = getExternalCacheDir();

        registerViews();
        setupViews();

        TitanCastNotification.setActivity(activity);

        checkWifiStatus();

        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(100);

        wFR = new WFStatusReceiver();

        registerReceiver(wFR, intentFilter);

        Details.setContext(this);

        if (!checkedUpdate) {
            checkForUpdate(false);
            checkedUpdate = true;
        }

        File f = new File(activity.getExternalCacheDir() + "titancast.apk");

        if (f.exists()) {
            f.delete();
            Log.d("titancast", "deleted previous app update file");
        }

        tryOpeningServer();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void fatalError(Exception e){
        unregisterReceiver(wFR);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        Intent myIntent = new Intent(this, ErrorStarting.class);
        myIntent.putExtra("error", sw.toString());

        startActivity(myIntent);
        finish();
    }

    public void btnGetStarted(View v){

        TitanCastNotification.showToast("Not yet implemented", Toast.LENGTH_LONG);

    }

    public void btnCheckUpdates(View v){

        checkForUpdate(true);

    }

    public void btnSettings(View v){

        TitanCastNotification.showToast("Not yet implemented", Toast.LENGTH_LONG);

    }

    public void btnWebsite(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://titancast.github.io/"));
        startActivity(intent);
    }

}
