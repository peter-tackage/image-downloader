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
            mStatusHandler.postStatusChanged(mRequest, Status.RUNNING);
            mDownloader.load(mRequest.getUri(), mRequest.getDestination());
            mStatusHandler.postStatusChanged(mRequest, Status.SUCCESSFUL);
        } catch (IOException e) {
            mStatusHandler.postStatusChanged(mRequest, Status.FAILED);
        }
    }
}
