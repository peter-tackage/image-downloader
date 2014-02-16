package com.moac.android.downloader.download;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FakeDownloader implements Downloader {

    public static final Uri FAILED_URI = Uri.parse("http://fail.com");
    public static final Uri SUCCESS_URI = Uri.parse("http://success.com");

    private static final String TAG = FakeDownloader.class.getSimpleName();

    private final long mTimeInSeconds;

    public FakeDownloader(long timeInSeconds) {
        mTimeInSeconds = timeInSeconds;
    }

    @Override
    public Response load(Uri uri, String destination) throws IOException {
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
        Response response = new Response();
        response.mIsSuccess = uri == SUCCESS_URI;
        return response;
    }
}
