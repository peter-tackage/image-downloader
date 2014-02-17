package com.moac.android.downloader.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.StatusHandler;

import java.util.UUID;

import javax.inject.Inject;

/*
 * Real implementation of the DownloadClient, most actions are
 * proxied through to the Scheduler.
 */
public class DefaultDownloadClient extends Binder implements DownloadClient {

    private static final String TAG = DefaultDownloadClient.class.getSimpleName();

    private final RequestStore mRequestStore;
    private final StatusHandler mStatusHandler;

    public DefaultDownloadClient(RequestStore requestStore, StatusHandler statusHandler) {
        Log.i(TAG, "Creating instance of DefaultDownloadClient: " + this);
        mRequestStore = requestStore;
        mStatusHandler = statusHandler;
    }

    @Override
    public void cancel(String id) {
        Log.i(TAG, "Cancelling download Id: " + id);
        mStatusHandler.moveToStatus(id, Status.CANCELLED);
    }

    @Override
    public Status getStatus(String id) {
        return mRequestStore.getStatus(id);
    }

}
