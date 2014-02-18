package com.moac.android.downloader.download;

import android.util.Log;

import java.io.*;

/*
 * A Runnable implementation that performs a download Request
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
            write(is, mRequest.getDestination(), networkResponse.getContentLength());

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


    public void write(InputStream inputStream, String fileDestination, long contentLength) throws IOException {

        File output = new File(fileDestination);
        if (output.exists() && output.isFile()) {
            output.delete();
        }

        BufferedOutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(output.getPath()));
            final int BUFFER_SIZE = 4096;
            byte[] buffer = new byte[BUFFER_SIZE];
            int totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1 && mRequest.getStatus() != Status.CANCELLED) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            fos.flush();
            if (totalBytesRead < contentLength) {
                throw new IOException("Read " + bytesRead + " from stream, was expecting " + contentLength);
            }
            Log.i(TAG, "Output file is apparently size: " + output.length());
        } finally {
            Utils.closeQuietly(fos);
        }
    }
}
