package com.moac.android.downloader.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadClient;
import com.moac.android.downloader.service.DownloadService;

/**
 * An unfinished idea.
 */
public class TheDownloader {

    private TheDownloader() {};

    public static TheDownloader from(Context context) {
        Context appContext = context.getApplicationContext();
        final Intent intent = new Intent(appContext, DownloadService.class);
        appContext.startService(intent);
        return new TheDownloader();
    }

    private DownloadClient mDownloadClient;
    private ServiceConnection mConnection = new ServiceConnection() {

        private static final String TAG = "DownloadClientServiceConnection";

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "onServiceConnected() - client is now available");
            mDownloadClient = (DefaultDownloadClient) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "onServiceDisconnected() - client is NOT available");
            mDownloadClient = null;
        }
    };
}
