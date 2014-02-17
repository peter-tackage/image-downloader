package com.moac.android.downloader.download;

import android.util.Log;

import java.io.*;

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
        InputStream is = null;
        try {
            moveToStatus(Status.RUNNING);
            NetworkResponse networkResponse = mDownloader.load(mRequest.getUri(), mRequest.getDestination());

            // Verify the network response
            if (networkResponse == null) {
                moveToStatus(Status.FAILED);
                return;
            }

            is = networkResponse.getInputStream();
            if (is == null) {
                moveToStatus(Status.FAILED);
                return;
            }
            Log.i(TAG, "Network response was: " + networkResponse.getContentLength());

            // Write stream to output destination file
            FileWriter writer = new FileWriter();
            writer.write(is, mRequest.getDestination(), networkResponse.getContentLength());

            // We finished without error
            moveToStatus(Status.SUCCESSFUL);

        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to complete download of %s to %s",
                    mRequest.getUri().toString(), mRequest.getDestination()), e);
            moveToStatus(Status.FAILED);
        } finally {
            Utils.closeQuietly(is);
        }
    }

    // Shorthand
    private void moveToStatus(Status status) {
        mStatusHandler.moveToStatus(mRequest.getId(), status);
    }
}
