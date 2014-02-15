package com.moac.android.downloader.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.injection.InjectingService;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class DownloadService extends InjectingService {

    private static final String TAG = DownloadService.class.getSimpleName();

    @Inject
    IBinder mDownloadClientBinder;

    @Inject
    Scheduler mScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand() - intent: " + intent);
        // The service is starting, due to a call to startService()
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the DownloadClient implementation
        return mDownloadClientBinder;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mScheduler.stop();
        super.onDestroy();
    }
}

