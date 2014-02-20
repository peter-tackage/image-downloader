package com.moac.android.downloader.download;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * A Runnable implementation that performs a download Request. A bit ugly.
 *
 * TODO Provide better logging and stronger definition of state changes
 */
public class Job implements Runnable {

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
            // Might have been cancelled - verify that download can proceed
            if (!moveToStatus(Status.RUNNING)) {
                return;
            }
            NetworkResponse networkResponse = mDownloader.load(mRequest.getUri(), mRequest.getDestination());

            // Quit if cancelled
            if (mRequest.isCancelled()) {
                return;
            }

            // Verify the network response
            if (networkResponse == null
                    || (is = networkResponse.getInputStream()) == null) {
                moveToStatus(Status.FAILED);
                return;
            }

            Log.i(TAG, "Network response was content-length: " + networkResponse.getContentLength());

            // Write stream to output destination file
            if (write(is, mRequest.getDestination(), networkResponse.getContentLength())) {
                // We finished without error or cancelling
                moveToStatus(Status.SUCCESSFUL);
            }

        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to complete download of %s to %s",
                    mRequest.getUri().toString(), mRequest.getDestination()), e);
            moveToStatus(Status.FAILED);
        } finally {
            Utils.closeQuietly(is);
        }
    }

    // Shorthand
    private boolean moveToStatus(Status status) {
        return mStatusHandler.moveToStatus(mRequest.getId(), status);
    }

    // Returns false if cancelled
    private boolean write(InputStream inputStream, String fileDestination, long contentLength) throws IOException {

        File output = new File(fileDestination);
        // Delete any existing file
        // TODO Rename instead?
        if (output.exists() && output.isFile()) {
            output.delete();
        }

        BufferedOutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(output.getPath()));
            final int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];
            int totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1 && !mRequest.isCancelled()) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            fos.flush();
            Log.i(TAG, "Output file is size: " + output.length());
            if (mRequest.isCancelled()) {
                // Clean up the potentially unfinished file
                File deleteFile = new File(output.getPath());
                if (deleteFile.delete()) {
                    Log.i(TAG, "Deleted unfinished download:" + output.getPath());
                }
                return false;
            } else if (totalBytesRead < contentLength) {
                throw new IOException("Read " + bytesRead + " from stream, was expecting " + contentLength);
            }
            return true;
        } finally {
            Utils.closeQuietly(fos);
        }
    }
}
