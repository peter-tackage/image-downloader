package com.moac.android.downloader.download;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        BufferedOutputStream fos = null;
        try {
            notifyStatus(Status.RUNNING);
            NetworkResponse networkResponse = mDownloader.load(mRequest.getUri(), mRequest.getDestination());

            // Verify the network response
            if (networkResponse == null) {
                notifyStatus(Status.FAILED);
                return;
            }

            is = networkResponse.getInputStream();
            if (is == null) {
                notifyStatus(Status.FAILED);
                return;
            }

            Log.i(TAG, "Network response was: " + networkResponse.getContentLength());

            File output=new File(mRequest.getDestination());
            if (output.exists() && output.isFile()) {
                output.delete();
            }

            fos = new BufferedOutputStream(new FileOutputStream(output.getPath()));
            final int BUFFER_SIZE = 4096;
            byte[] buffer = new byte[BUFFER_SIZE];
            int totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead+=bytesRead;
            }
            if(totalBytesRead < networkResponse.getContentLength()) {
                Log.e(TAG, "Got " + bytesRead + " was expecting " + networkResponse.getContentLength());
                notifyStatus(Status.FAILED);
                return;
            }
            notifyStatus(Status.SUCCESSFUL);
        } catch (IOException e) {
            notifyStatus(Status.FAILED);
        } finally {
            Utils.closeQuietly(is);
            Utils.closeQuietly(fos);
        }
    }

    private void notifyStatus(Status status) {
        mStatusHandler.handleStatusChanged(mRequest, status);
    }
}
