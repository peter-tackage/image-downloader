package com.moac.android.downloader.download;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A fake {@link com.moac.android.downloader.download.Downloader}
 * to test the service mechanisms
 * <p/>
 * Returns an empty input stream
 */
public class FakeDownloader implements Downloader {

    private static final String TAG = FakeDownloader.class.getSimpleName();

    private final long mTimeInSeconds;

    public FakeDownloader(long timeInSeconds) {
        mTimeInSeconds = timeInSeconds;
    }

    @Override
    public NetworkResponse load(Uri uri, String destination) throws IOException {
        Log.i(TAG, "Fake downloading start: " + uri.toString()
                + " to: " + destination
                + " will take: " + mTimeInSeconds + " sec");
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(mTimeInSeconds));
        } catch (InterruptedException e) {
            Log.i(TAG, "Fake downloading was interrupted!!");
            e.printStackTrace();
        }
        Log.i(TAG, "Fake downloading end: " + uri.toString());
        NetworkResponse networkResponse = new NetworkResponse(null, 0);
        return networkResponse;
    }
}
