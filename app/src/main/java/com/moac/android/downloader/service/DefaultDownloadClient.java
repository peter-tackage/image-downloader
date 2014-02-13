package com.moac.android.downloader.service;

import android.os.Binder;
import android.util.Log;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.RequestScheduler;
import com.moac.android.downloader.download.Status;

import java.util.EnumSet;

import javax.inject.Inject;

public class DefaultDownloadClient extends Binder implements DownloadClient {

    private static final String TAG = DefaultDownloadClient.class.getSimpleName();

    private final RequestScheduler mRequestScheduler;

    @Inject
    public DefaultDownloadClient(RequestScheduler scheduler) {
        mRequestScheduler = scheduler;
    }

    @Override
    public long download(Request request) {
        Log.i(TAG, "Enqueueing request: " + request.getUri());
        mRequestScheduler.submit(request);
        return -1;
    }

    @Override
    public void cancel(long id) {
        Log.i(TAG, "Cancelling download Id: " + id);
        // FIXME Have a way of cancelling jobs
    }

    @Override
    public long[] getRequests(EnumSet<Status> statuses) {
        return new long[0];
    }

}
