package com.moac.android.downloader.download;

import android.net.Uri;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class FakeDownloader implements Downloader {

    private static final String TAG = FakeDownloader.class.getSimpleName();

    private final long mTimeInSeconds;

    public FakeDownloader(long timeInSeconds) {
        mTimeInSeconds = timeInSeconds;
    }

    @Override
    public void load(Uri uri, String destination) {
        Log.i(TAG, "Fake downloading start: " + uri.toString()
                + " to: " + destination
                + " will take:" + mTimeInSeconds + " sec");
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(mTimeInSeconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Fake downloading end: " + uri.toString());
    }
}
