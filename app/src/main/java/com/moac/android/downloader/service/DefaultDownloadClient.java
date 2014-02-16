package com.moac.android.downloader.service;

import android.os.Binder;
import android.util.Log;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.download.Status;

import java.util.EnumSet;

import javax.inject.Inject;

/*
 * Real implementation of the DownloadClient, most actions are
 * proxied through to the Scheduler.
 */
public class DefaultDownloadClient extends Binder implements DownloadClient {

    private static final String TAG = DefaultDownloadClient.class.getSimpleName();

    private final Scheduler mScheduler;

    @Inject
    public DefaultDownloadClient(Scheduler scheduler) {
        Log.i(TAG, "Creating instance of DefaultDownloadClient: " + this);
        mScheduler = scheduler;
    }

    @Override
    public void cancel(String id) {
        Log.i(TAG, "Cancelling download Id: " + id);
        mScheduler.cancel(id);
    }

    @Override
    public Status getStatus(String id) {
        return mScheduler.getStatus(id);
    }

}
