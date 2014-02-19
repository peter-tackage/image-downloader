package com.moac.android.downloader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.service.DownloadClient;
import com.moac.android.downloader.service.DownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/*
 * Bind during onResume() to get DownloadClient in order to display
 * the state of any downloads in progress.
 *
 * Start a download with an Intent including
 * - Remote Uri
 * - Local destination location
 * - Tracking id
 */
public class TestActivity extends Activity {

    /*
     * Test data
     *
     * A mock datasource that provides the Uri for an image with a unique identifier
     */
    private static final String TRACKING_ID_1 = "imageId1";
    private static final String TRACKING_ID_2 = "imageId2";
    private static HashMap<String, Uri> FAKE_DATASOURCE = new HashMap<String, Uri>();

    static {
        FAKE_DATASOURCE.put(TRACKING_ID_1, Uri.parse("http://upload.wikimedia.org/wikipedia/commons/2/21/Adams_The_Tetons_and_the_Snake_River.jpg"));
        FAKE_DATASOURCE.put(TRACKING_ID_2, Uri.parse("http://upload.wikimedia.org/wikipedia/commons/5/57/ECurtis.jpg"));
    }

    private static final String TAG = TestActivity.class.getSimpleName();
    private static final String SUBMITTED_DOWNLOADS_KEY = "submittedDownloads";

    private DownloadClient mDownloadClient;
    private boolean mIsBound;

    // Test views, implementation is purely for demonstration purposes!
    private ViewGroup mDemoPic1Container, mDemoPic2Container;
    private ViewGroup mDemoPic1ProgressIndicator, mDemoPic2ProgressIndicator;

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
            restoreViewState(mDemoPic1Container, false);
            restoreViewState(mDemoPic2Container, false);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected() - client is NOT available");
            mDownloadClient = null;
            mIsBound = false;
        }
    };

    private View.OnClickListener imageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsBound) {
                String trackingId = (String) v.getTag();
                Log.i("onClick()", "Clicked on imageId: " + trackingId);
                Uri uri = FAKE_DATASOURCE.get(trackingId);
                if (mSubmittedDownloads.contains(trackingId)) {
                    mDownloadClient.cancel(trackingId);
                    mSubmittedDownloads.remove(trackingId);
                } else {
                    mSubmittedDownloads.add(trackingId);
                    Intent i = new Intent(TestActivity.this, DownloadService.class);
                    i.putExtra(DownloadService.DOWNLOAD_ID, trackingId);
                    i.putExtra(DownloadService.REMOTE_LOCATION, uri.toString());
                    i.putExtra(DownloadService.LOCAL_LOCATION, generateUniqueTestFilename());
                    startService(i);
                }
            }
        }
    };

    // Align the view states with the current download status
    private void restoreViewState(View container, boolean showToast) {
        String id = (String) container.getTag();
        onRequestStatusChanged(id, mDownloadClient.getStatus(id), showToast);
    }

    private void onRequestStatusChanged(String id, Status status, boolean showToast) {
        Log.i(TAG, "onRequestStatusChanged() - id: " + id + " is now: " + status);
        switch (status) {
            case UNKNOWN:
                getIndicatorView(id).setVisibility(View.GONE);
                mSubmittedDownloads.remove(id);
                break;
            case CREATED:
            case PENDING:
            case RUNNING:
                getIndicatorView(id).setVisibility(View.VISIBLE);
                if (!mSubmittedDownloads.contains(id)) {
                    mSubmittedDownloads.add(id);
                }
                break;
            case CANCELLED:
                if(showToast)
                    Toast.makeText(getApplicationContext(), "Download cancelled", Toast.LENGTH_SHORT).show();
                getIndicatorView(id).setVisibility(View.GONE);
                mSubmittedDownloads.remove(id);
                break;
            case SUCCESSFUL:
                if(showToast)
                    Toast.makeText(getApplicationContext(), "Downloaded to pictures folder", Toast.LENGTH_SHORT).show();
                getIndicatorView(id).setVisibility(View.GONE);
                mSubmittedDownloads.remove(id);
                break;
            case FAILED:
                if(showToast)
                    Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
                getIndicatorView(id).setVisibility(View.GONE);
                mSubmittedDownloads.remove(id);
                break;
            default:
        }
    }

    private View getIndicatorView(String id) {
        if (id.equals(TRACKING_ID_1)) {
            return mDemoPic1ProgressIndicator;
        } else if (id.equals(TRACKING_ID_2)) {
            return mDemoPic2ProgressIndicator;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Image 1
        mDemoPic1ProgressIndicator = (ViewGroup) findViewById(R.id.vg_progress_indicator_1);
        mDemoPic1Container = (ViewGroup) findViewById(R.id.vg_demo_pic1);
        // Associate the test download tracking id with the View.
        mDemoPic1Container.setTag(TRACKING_ID_1);
        mDemoPic1Container.setOnClickListener(imageOnClickListener);

        // Image 2
        mDemoPic2ProgressIndicator = (ViewGroup) findViewById(R.id.vg_progress_indicator_2);
        mDemoPic2Container = (ViewGroup) findViewById(R.id.vg_demo_pic2);
        // Associate the test download tracking id with the View.
        mDemoPic2Container.setTag(TRACKING_ID_2);
        mDemoPic2Container.setOnClickListener(imageOnClickListener);
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
                String trackingId = bundle.getString(DownloadService.DOWNLOAD_ID);
                Status status = (Status) bundle.get(DownloadService.STATUS);
                String localLocation = (String) bundle.get(DownloadService.LOCAL_LOCATION);
                Log.i(TAG, "Received event for downloadId: " + trackingId + " status: " + status + " local: " + localLocation);
                onRequestStatusChanged(trackingId, status, true);
                if (!TextUtils.isEmpty(localLocation)) {
                    triggerMediaScan(localLocation);
                }
            }
        }
    };

    private void triggerMediaScan(String localLocation) {
        File file = new File(localLocation);
        Log.i(TAG, "Got file size in activity: " + file.length());
        MediaScannerConnection.scanFile(this,
                new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("onScanCompleted", "Scanned " + path);
                    }
                });
    }

    private static String generateUniqueTestFilename() {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DownloadedPicture-" + UUID.randomUUID().toString() + ".jpg");
        return file.toString();
    }
}
