package com.moac.android.downloader.download;

import android.util.Log;

import java.io.IOException;

/*
 * A Runnable implementation that performs a download Request
 */
class Job implements Runnable {

    private static final String TAG = Job.class.getSimpleName();

    private final Downloader mDownloader;
    private final Request mRequest;
    private final StatusHandler mStatusHandler;

    public Job(Request request, Downloader downloader, StatusHandler statusHandler) {
        mRequest = request;
        mDownloader = downloader;
        mStatusHandler = statusHandler;
    }

    @Override
    public void run() {
        Log.i(TAG, "Job being run on thread: " + Thread.currentThread());
        try {
            sendStatus(Status.RUNNING);
            mDownloader.load(mRequest.getUri(), mRequest.getDestination());
            sendStatus(Status.SUCCESSFUL);
        } catch (IOException e) {
            sendStatus(Status.FAILED);
        }
    }

    private void sendStatus(Status status) {
        mStatusHandler.handleStatusChanged(mRequest, status);
    }
}
