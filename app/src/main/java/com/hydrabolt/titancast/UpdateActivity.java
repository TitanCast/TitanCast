package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class UpdateActivity extends Activity {

    public static boolean open = false;
    public static String version = "";

    private static String versionName, download, buildType;
    private static int intVersion;

    private static ProgressBar loading;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_update);
        open = true;

        loading = (ProgressBar) findViewById(R.id.loadingStatus);

        loading.setVisibility(View.GONE);

        version = getIntent().getStringExtra("version");

        TextView view = (TextView) findViewById(R.id.versionName);

        versionName = getIntent().getStringExtra("version");
        intVersion = getIntent().getIntExtra("intVersion", 0);
        download = getIntent().getStringExtra("download");
        buildType = getIntent().getStringExtra("buildType");


        view.setText(versionName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void startDownload(View view) {

        Button b = (Button) findViewById(R.id.updateButton);
        b.setEnabled(false);
        b.setText("PLEASE WAIT...");
        loading.setVisibility(View.VISIBLE);

        try {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    File file;
                    file = new File(getExternalCacheDir(), "titancast.apk");

                    try {
                        FileUtils.copyURLToFile(new URL(download), file);
                    } catch (IOException e) {
                        TitanCastNotification.showToast("Error Updating, visit site and manually update.", Toast.LENGTH_LONG);
                        e.printStackTrace();
                        finish();
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
            TitanCastNotification.showToast("Error Updating, visit site and manually update.", Toast.LENGTH_LONG);
            finish();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        open = false;
    }
}
