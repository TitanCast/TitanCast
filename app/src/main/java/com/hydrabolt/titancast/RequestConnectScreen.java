package com.hydrabolt.titancast;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class RequestConnectScreen extends AppCompatActivity {

    private TextView appName, appDesc;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_connect_screen);

        if(Details.connected)
            finish();

        appName = (TextView) findViewById(R.id.appName);
        appDesc = (TextView) findViewById(R.id.appDesc);

        setTitle("Casting Permission");

        Intent i = getIntent();
        String appName = i.getStringExtra("app_name");
        String appDesc = i.getStringExtra("app_desc");
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void acceptClicked(View v){
        MainActivity.getServer().acceptRequest(index);
        finish();
    }
    public void ignoreClicked(View v){
        MainActivity.getServer().rejectRequest(index);
        finish();
    }

    @Override
    public void onBackPressed(){
        MainActivity.getServer().rejectRequest(index);
        finish();
    }
}
