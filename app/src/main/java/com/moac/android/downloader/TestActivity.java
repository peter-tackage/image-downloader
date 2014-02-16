package com.moac.android.downloader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.service.DownloadClient;
import com.moac.android.downloader.service.DownloadService;

import java.io.File;
import java.util.ArrayList;

/*
 * Bind during onResume() to get DownloadClient in order to display
 * the state of any downloads in progress.
 *
 * Start a download with an Intent including
 * - Remote Uri
 * - Local destination location
 * - Tracking id
 *
 * TODO All submitted downloads could be stored in a database that
 * could be queried for state instead of binding to the service.
 *
 * TODO Updates could also be driven from this content provider/database without having
 * to use broadcasts
 */
public class TestActivity extends Activity {

    private static final String TAG = TestActivity.class.getSimpleName();
    private static final String IMAGE_URL_1 = "http://upload.wikimedia.org/wikipedia/commons/2/21/Adams_The_Tetons_and_the_Snake_River.jpg";
    private static final String SUBMITTED_DOWNLOADS_KEY = "submittedDownloads";

    private DownloadClient mDownloadClient;
    private boolean mIsBound;

    // Test views, implementation is purely for demonstration purposes!
    // TODO This would be done better with a custom view
    private ViewGroup mDemoPic1Container;
    private ViewGroup mDemoPic1ProgressIndicator;

    private ArrayList<String> mSubmittedDownloads = new ArrayList<String>();
    private ServiceConnection mConnection = new ServiceConnection() {

        private static final String TAG = "DownloadClientServiceConnection";

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "onServiceConnected() - client is now available");
            mDownloadClient = (DownloadClient) service;
            mIsBound = true;
            // We are bound, so we can query to find state
            // TODO This would be better done by querying a DB
            restoreViewState(mDemoPic1Container);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected() - client is NOT available");
            mDownloadClient = null;
            mIsBound = false;
            // TODO disable user download control here
        }
    };

    // Align the view states with the current download status
    private void restoreViewState(View container) {
        String uriId = (String) container.getTag();
        onRequestStatusChanged(uriId, mDownloadClient.getStatus(uriId));
    }

    private void onRequestStatusChanged(String id, Status status) {
        Log.i(TAG, "onRequestStatusChanged() - id: " + id + " is now: " + status);
        switch (status) {
            case CREATED:
            case PENDING:
            case RUNNING:
                getIndicatorView(id).setVisibility(View.VISIBLE);
                if (!mSubmittedDownloads.contains(id)) {
                    mSubmittedDownloads.add(id);
                }
                break;
            default:
                getIndicatorView(id).setVisibility(View.GONE);
                mSubmittedDownloads.remove(id);
        }
    }

    private View getIndicatorView(String id) {
        if (id.equals(IMAGE_URL_1)) {
            return mDemoPic1ProgressIndicator;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDemoPic1ProgressIndicator = (ViewGroup) findViewById(R.id.vg_progress_indicator);
        mDemoPic1Container = (ViewGroup) findViewById(R.id.vg_demo_pic1);
        // Associate the download Uri with the View.
        mDemoPic1Container.setTag(IMAGE_URL_1);
        mDemoPic1Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) {
                    String uriId = (String) v.getTag();
                    if (mSubmittedDownloads.contains(uriId)) {
                        mDownloadClient.cancel(uriId);
                    } else {
                        Intent i = new Intent(TestActivity.this, DownloadService.class);
                        Uri uri = Uri.parse(uriId);
                        i.putExtra(DownloadService.DOWNLOAD_ID, uriId);
                        i.putExtra(DownloadService.REMOTE_LOCATION, uri.toString());
                        i.putExtra(DownloadService.LOCAL_LOCATION, makeTestFile());
                        startService(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mStatusReceiver, new IntentFilter(DownloadService.STATUS_EVENTS));
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStatusReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SUBMITTED_DOWNLOADS_KEY, mSubmittedDownloads);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSubmittedDownloads = savedInstanceState.getStringArrayList(SUBMITTED_DOWNLOADS_KEY);
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String downloadId = bundle.getString(DownloadService.DOWNLOAD_ID);
                Status status = (Status) bundle.get(DownloadService.STATUS);
                Log.i(TAG, "Received event for downloadId: " + downloadId + " status: " + status);
                onRequestStatusChanged(downloadId, status);
            }
        }
    };

    private String makeTestFile() {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture1-"+System.currentTimeMillis()+".jpg");
        return file.toString();
    }
}
