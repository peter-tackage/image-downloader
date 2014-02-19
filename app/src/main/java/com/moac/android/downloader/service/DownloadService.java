package com.moac.android.downloader.service;

import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.DownloadServiceModule;
import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.Job;
import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.injection.InjectingService;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

public class DownloadService extends InjectingService {

    // Broadcast and Intent extras
    public static final String STATUS_EVENTS = "com.moac.android.downloader.STATUS_EVENTS";
    public static final String DOWNLOAD_ID = "com.moac.android.downloader.DOWNLOAD_ID";
    public static final String STATUS = "com.moac.android.downloader.STATUS";
    public static final String REMOTE_LOCATION = "com.moac.android.downloader.REMOTE_LOCATION";
    public static final String LOCAL_LOCATION = "com.moac.android.downloader.LOCAL_LOCATION";

    // Logging
    private static final String TAG = DownloadService.class.getSimpleName();

    @Inject
    RequestStore mRequestStore;

    @Inject
    ThreadPoolExecutor mRequestExecutor;

    @Inject
    IBinder mDownloadClient;

    @Inject
    StatusHandler mStatusHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand() - intent: " + intent);
        if (intent != null) {
            // Extract the request details from the intent
            String downloadId = intent.getStringExtra(DOWNLOAD_ID);
            String remoteLocation = intent.getStringExtra(REMOTE_LOCATION);
            String localLocation = intent.getStringExtra(LOCAL_LOCATION);

            Request request = mRequestStore.getRequest(downloadId);
            if (request == null) {
                request = new Request(downloadId, Uri.parse(remoteLocation), localLocation);
                mRequestStore.add(request);
            }
            // Check we are allow to proceed with the request
            if (mStatusHandler.moveToStatus(downloadId, Status.PENDING)) {
                submit(request);
            }

        } else {
            // TODO When restarted due to kill we should check for any unfinished downloads
            // and then invoke startService again on ourselves.
            // This requires a persistent store implementation
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadClient;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mRequestExecutor.shutdown();
        super.onDestroy();
    }

    private void submit(Request request) {
        Log.i(TAG, "Creating download job for id: " + request.getId());
        // Submit to executor queue
        Job job = new Job(request, getObjectGraph().get(Downloader.class), mStatusHandler);
        mRequestExecutor.submit(job);
    }

    @Override
    public List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new DownloadServiceModule(this));
        return modules;
    }
}