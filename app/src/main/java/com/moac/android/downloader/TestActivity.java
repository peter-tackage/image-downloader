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
import java.util.HashMap;
import java.util.UUID;

/*
 * TestActivity to drive the DownloadService
 *
 * Binds during onResume() to get DownloadClient in order to display
 * the state of any downloads that were previously started.
 *
 * Registers a LocalBroadcastReceiver during onResume() to receive updates
 * for in-progress requests.
 *
 * This implementation assigns a new unique filename for each request
 *
 * Start a download with an Intent including
 * - Tracking id
 * - Remote Uri
 * - Local destination location
 * - Display name
 * - Media Type (not yet supported)
 *
 * Media scanner is automatically triggered by the Service
 *
 * TODO Use a GridView and custom compound View to hold the images/overlay
 * TODO Custom view would fire start/cancel events to a listener
 */
public class TestActivity extends Activity {

    /*
     * Test data
     *
     * A fake data source that provides the Uri for an image with a unique identifier
     *
     * Test large files like -
     *
     * (Warning TIF viewing not supported) http://imgsrc.hubblesite.org/hu/db/images/hs-2004-32-d-full_tif.tif
     *
     *
     */
    private static final String TRACKING_ID_1 = "imageId1";
    private static final String TRACKING_ID_2 = "imageId2";
    private static HashMap<String, Uri> FAKE_DATASOURCE = new HashMap<String, Uri>();
    static {
        FAKE_DATASOURCE.put(TRACKING_ID_1, Uri.parse("http://upload.wikimedia.org/wikipedia/commons/2/21/Adams_The_Tetons_and_the_Snake_River.jpg"));
        FAKE_DATASOURCE.put(TRACKING_ID_2, Uri.parse("http://upload.wikimedia.org/wikipedia/commons/5/57/ECurtis.jpg"));
    }

    private static final String TAG = TestActivity.class.getSimpleName();

    // Direct interface to the Service
    private DownloadClient mDownloadClient;
    private boolean mIsBound;

    // Test views, implementation is purely for demonstration purposes!
    private ViewGroup mDemoPic1Container, mDemoPic2Container;
    private ViewGroup mDemoPic1ProgressIndicator, mDemoPic2ProgressIndicator;

    private ServiceConnection mConnection = new ServiceConnection() {
        private static final String CONNECTION_TAG = "DownloadClientServiceConnection";
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(CONNECTION_TAG, "onServiceConnected() - client is now available");
            mDownloadClient = (DownloadClient) service;
            mIsBound = true;
            // We are bound, so we can query to find state
            restoreViewState(mDemoPic1Container);
            restoreViewState(mDemoPic2Container);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(CONNECTION_TAG, "onServiceDisconnected() - client is NOT available");
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
                Status status = mDownloadClient.getStatus(trackingId);
                if (status == Status.UNKNOWN || status.ordinal() >= Status.CANCELLED.ordinal()) {
                    // Create the start intent
                    Intent i = new Intent(TestActivity.this, DownloadService.class);
                    i.putExtra(DownloadService.DOWNLOAD_ID, trackingId);
                    i.putExtra(DownloadService.REMOTE_LOCATION, uri.toString());
                    String destinationFilename = generateUniqueTestFilename();
                    i.putExtra(DownloadService.LOCAL_LOCATION, destinationFilename);
                    i.putExtra(DownloadService.DISPLAY_NAME, new File(destinationFilename).getName());
                    startService(i);
                } else {
                    mDownloadClient.cancel(trackingId);
                }
            }
        }
    };

    // Align the view states with the current download status
    private void restoreViewState(View container) {
        String id = (String) container.getTag();
        onRequestStatusChanged(id, mDownloadClient.getStatus(id));
    }

    private void onRequestStatusChanged(String id, Status status) {
        Log.i(TAG, "onRequestStatusChanged() - id: " + id + " is now: " + status);
        switch (status) {
            case CREATED:
            case PENDING:
            case RUNNING:
                getIndicatorView(id).setVisibility(View.VISIBLE);
                break;
            case UNKNOWN:
            case CANCELLED:
            case SUCCESSFUL:
            case FAILED:
                getIndicatorView(id).setVisibility(View.GONE);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Request state: " + status);
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

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String trackingId = bundle.getString(DownloadService.DOWNLOAD_ID);
                Status status = (Status) bundle.get(DownloadService.STATUS);
                String localLocation = (String) bundle.get(DownloadService.LOCAL_LOCATION);
                Log.i(TAG, "Received event for downloadId: " + trackingId + " status: " + status + " local: " + localLocation);
                onRequestStatusChanged(trackingId, status);
            }
        }
    };

    private String generateUniqueTestFilename() {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DownloadedPicture-" + UUID.randomUUID().toString() + ".jpg");
        return file.toString();
    }
}
