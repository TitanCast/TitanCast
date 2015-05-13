package com.hydrabolt.titancast;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class RequestConnectScreen extends AppCompatActivity {

    int index = 0;
    private TextView appName, appDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_connect_screen);

        if (Details.connected())
            finish();

        appName = (TextView) findViewById(R.id.appName);
        appDesc = (TextView) findViewById(R.id.appDesc);

        setTitle("Casting Permission");

        Intent i = getIntent();
        String appName = i.getStringExtra("app_name");
        String appDesc = i.getStringExtra("app_desc");
        String appIcon = i.getStringExtra("app_icon");

        String imageDataBytes = appIcon.substring(appIcon.indexOf(",") + 1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        ImageView cimg = (ImageView)findViewById(R.id.appIcon);
        cimg.setImageBitmap( BitmapFactory.decodeStream(stream) );
        cimg.setMaxWidth(32);
        cimg.setMaxHeight(32);

        index = i.getIntExtra("client_id", 0);

        this.appName.setText(appName);
        this.appDesc.setText(appDesc);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void acceptClicked(View v) {
        MainActivity.getServer().acceptRequest(index);
        finish();
    }

    public void ignoreClicked(View v) {
        MainActivity.getServer().rejectRequest(index);
        finish();
    }

    @Override
    public void onBackPressed() {
        MainActivity.getServer().rejectRequest(index);
        finish();
    }
}
