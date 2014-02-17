package com.moac.android.downloader.service;

import android.os.Binder;
import android.util.Log;

import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.StatusHandler;

/*
 * Real implementation of the DownloadClient
 */
public class DefaultDownloadClient extends Binder implements DownloadClient {

    private static final String TAG = DefaultDownloadClient.class.getSimpleName();

    private final RequestStore mRequestStore;
    private final StatusHandler mStatusHandler;

    public DefaultDownloadClient(RequestStore requestStore, StatusHandler statusHandler) {
        mRequestStore = requestStore;
        mStatusHandler = statusHandler;
    }

    @Override
    public boolean cancel(String id) {
        Log.i(TAG, "Attempting to cancel downloadId: " + id);
        return mStatusHandler.moveToStatus(id, Status.CANCELLED);
    }

    @Override
    public Status getStatus(String id) {
        return mRequestStore.getStatus(id);
    }

}
