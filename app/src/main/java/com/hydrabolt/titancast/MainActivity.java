package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    private static TextView statusSubtitle, status;
    private static ProgressBar progressBar;
    private static boolean connected = false;
    public static Context ctx;
    private static WSServer server;
    private static Activity a;

    private void registerViews(){
        statusSubtitle = (TextView) findViewById(R.id.statusSubtitle);
        status = (TextView) findViewById(R.id.status);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void setupViews(){
        progressBar.setVisibility(View.VISIBLE);
        statusSubtitle.setText("just a second");
        status.setText("please wait");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();

        registerViews();
        setupViews();

        wifiUpdate();

        Details.setContext(this);
        a = this;

        server = new WSServer(new InetSocketAddress(25517), ctx);
        server.start();

        checkForUpdate(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    public static void wifiStateChanged(int state, int ip) {

        connected = (state == 2);
        status.setTypeface(Typeface.DEFAULT);

        if(state == 2){
            statusSubtitle.setText("connect to");
            status.setText( FormattingTools.getIP(ip) );
            setProgressHidden(true);

            status.setTypeface(Typeface.MONOSPACE);

        }else if(state == 1){
            statusSubtitle.setText("nearly done");
            status.setText("connecting");
            setProgressHidden(false);
        }else{
            statusSubtitle.setText("uh-oh");
            status.setText("connect to wi-fi");
            setProgressHidden(true);
        }

    }

    public static void wifiUpdate(){

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        WifiManager wm = (WifiManager) ctx.getSystemService(WIFI_SERVICE);

        if (ni.isConnected()) {

            int ip = wm.getConnectionInfo().getIpAddress();
            wifiStateChanged(2, ip);

        }else if(ni.isConnectedOrConnecting()){
            wifiStateChanged(1, -1);
        }else{
            wifiStateChanged(0, -1);
        }

    }

    private static void setProgressHidden(boolean hidden){
        if(hidden)
            progressBar.setVisibility(View.INVISIBLE);
        else
            progressBar.setVisibility(View.VISIBLE);
    }

    public static WSServer getServer(){
        return server;
    }

    public static void checkForUpdate(boolean override){
        if(override){
            Details.shownUpdate = false;
        }
        new Thread(new CheckUpdate(a, override)).start();
    }

}
