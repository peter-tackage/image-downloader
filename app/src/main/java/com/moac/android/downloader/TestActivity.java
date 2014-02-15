package com.moac.android.downloader;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadClient;
import com.moac.android.downloader.service.DownloadService;

public class TestActivity extends Activity {

    private static final String TAG = TestActivity.class.getSimpleName();

    Button mSubmitButton;
    TextView mStatusTextView;

    private DownloadClient mDownloadClient;
    private ToggleButton mServiceBindToggle;
    private ToggleButton mServiceToggle;
    private ServiceConnection mConnection = new ServiceConnection() {

        private static final String TAG = "DownloadClientServiceConnection";

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "onServiceConnected() - client is now available");
            mDownloadClient = (DefaultDownloadClient) service;
            mStatusTextView.setText("Client connected");
            mServiceBindToggle.setChecked(true);
            mServiceToggle.setChecked(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "onServiceDisconnected() - client is NOT available");
            mDownloadClient = null;
            mStatusTextView.setText("Client disconnected");
            mServiceBindToggle.setChecked(false);
            mServiceToggle.setChecked(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSubmitButton = (Button) findViewById(R.id.button_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadClient != null) {
                    Uri uri = Uri.parse("http://www.thisistestistestistest.com/pic-" + System.currentTimeMillis() + ".jpg");
                    mDownloadClient.download(new Request(uri, "nowhere"));
                } else {
                    Toast.makeText(TestActivity.this, "Download Client is not ready", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mStatusTextView = (TextView) findViewById(R.id.textView_status);
        mServiceBindToggle = (ToggleButton) findViewById(R.id.toggle_service_bind_btn);
        mServiceBindToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Binding connection");
                    bindService(new Intent(TestActivity.this, DownloadService.class), mConnection, Context.BIND_AUTO_CREATE);
                } else {
                    Log.i(TAG, "Unbinding connection");
                    unbindService(mConnection);
                }
            }
        });
        mServiceToggle = (ToggleButton) findViewById(R.id.toggle_service_btn);
        mServiceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Starting service");
                    startService(new Intent(TestActivity.this, DownloadService.class));
                } else {
                    Log.i(TAG, "Stopping service");
                    stopService(new Intent(TestActivity.this, DownloadService.class));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        /// FIXME Fails if not set
        if (mConnection != null) {
            unbindService(mConnection);
        }
        super.onDestroy();
    }
}
