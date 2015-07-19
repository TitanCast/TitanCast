package com.hydrabolt.titancast;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class UpdateActivity extends ActionBarActivity {

    public static boolean open = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        open = true;
        setContentView(R.layout.activity_update);

        TextView view = (TextView) findViewById(R.id.updateText);

        view.setText("An update to version " + getIntent().getStringExtra("version") + " is available. Please update by tapping the button below");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void startDownload(View view){

        Button b = (Button) findViewById(R.id.updateButton);
        b.setEnabled(false);
        b.setText("PLEASE WAIT...");

            try {

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    File file;
                    file = new File(getExternalCacheDir(), "titancast.apk");

                    try {
                        FileUtils.copyURLToFile(new URL("http://titancast.github.io/download/v/titancast.0.0.2.apk"), file);
                    } catch (IOException e) {
                        TitanCastNotification.showToast("Error Updating, visit site and manually update.", Toast.LENGTH_LONG);
                        Log.d("titancast-update", "error - "+e.getLocalizedMessage());
                        finish();
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType( Uri.fromFile(file) , "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            thread.start();

        } catch ( Exception e ) {
            TitanCastNotification.showToast("Error Updating, visit site and manually update.", Toast.LENGTH_LONG);
            finish();
        }

    }

    @Override
    public void onStop () {
        super.onStop();
        open = false;
    }
}
