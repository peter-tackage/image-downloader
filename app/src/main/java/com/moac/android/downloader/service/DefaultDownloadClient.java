package com.moac.android.downloader.service;

import android.os.Binder;
import android.util.Log;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.download.Status;

import java.util.EnumSet;

import javax.inject.Inject;

/*
 * Real implementation of the DownloadClient, submits Requests
 * to be scheduled for download.
 *
 * TODO Have a way of cancelling jobs
 * TOOD GetRequests
 *
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
    public long download(Request request) {
        Log.i(TAG, "Enqueueing request: " + request.getUri());
        mScheduler.submit(request);
        return -1;
    }

    @Override
    public void cancel(long id) {
        Log.i(TAG, "Cancelling download Id: " + id);
    }

    @Override
    public long[] getRequests(EnumSet<Status> statuses) {
        return new long[0];
    }

    @Override
    public void addListener(DownloadListener listener) {
       mScheduler.addEventListener(listener);
    }

    @Override
    public void removeListener(DownloadListener listener) {
        mScheduler.removeEventListener(listener);
    }

}
