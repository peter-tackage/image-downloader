package com.moac.android.downloader.service;

import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.injection.InjectingService;

import javax.inject.Inject;

public class DownloadService extends InjectingService {

    // Broadcast events
    public static final String STATUS_EVENTS = "com.moac.android.downloader.STATUS_EVENTS";
    public static final String DOWNLOAD_ID = "com.moac.android.downloader.DOWNLOAD_ID";
    public static final String STATUS = "com.moac.android.downloader.STATUS";

    // Service actions
    public static final int REQUEST_SUBMIT = 1;

    // Logging
    private static final String TAG = DownloadService.class.getSimpleName();

    @Inject
    Scheduler mScheduler;

    IBinder mDownloadClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadClient = new DefaultDownloadClient(mScheduler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand() - intent: " + intent);
        // FIXME Destination, STICKY IS WRONG
        String downloadId = intent.getStringExtra(DOWNLOAD_ID);
        Uri uri = intent.getData();
        String destination = "nowhere";
        mScheduler.submit(new Request(downloadId, uri, destination));
        return START_STICKY;
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

