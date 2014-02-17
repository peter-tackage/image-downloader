package com.moac.android.downloader.service;

import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.download.DownloaderFactory;
import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.injection.InjectingService;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class DownloadService extends InjectingService {

    // Broadcast events
    public static final String STATUS_EVENTS = "com.moac.android.downloader.STATUS_EVENTS";
    public static final String DOWNLOAD_ID = "com.moac.android.downloader.DOWNLOAD_ID";
    public static final String STATUS = "com.moac.android.downloader.STATUS";
    public static final String REMOTE_LOCATION = "com.moac.android.downloader.REMOTE_LOCATION";
    public static final String LOCAL_LOCATION = "com.moac.android.downloader.LOCAL_LOCATION";

    // Service actions
    public static final int REQUEST_SUBMIT = 1;

    // Logging
    private static final String TAG = DownloadService.class.getSimpleName();

    @Inject
    RequestStore mRequestStore;

    @Inject
    DownloaderFactory mDownloaderFactory;

    @Inject
    ExecutorService mExecutor;

    IBinder mDownloadClient;

    Scheduler mScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestStore = new RequestStore(this);
        mScheduler = new Scheduler(mRequestStore, mExecutor, mDownloaderFactory);
        mDownloadClient = new DefaultDownloadClient(this, mRequestStore, mScheduler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand() - intent: " + intent);
        if (intent != null) {
            String downloadId = intent.getStringExtra(DOWNLOAD_ID);
            String remoteLocation = intent.getStringExtra(REMOTE_LOCATION);
            String localLocation = intent.getStringExtra(LOCAL_LOCATION);
            // TODO Check if it is running being processed or has finished
            Request request = mRequestStore.create(downloadId, Uri.parse(remoteLocation), localLocation);
            mScheduler.submit(request);
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadClient;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mScheduler.stop();
        super.onDestroy();
    }
}