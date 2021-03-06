package com.moac.android.downloader.test;

import android.util.Log;

import com.moac.android.downloader.download.Job;
import com.moac.android.downloader.download.Status;

import java.util.concurrent.TimeUnit;

/*
 * A Fake Runnable implementation that performs a download Request
 */
@Deprecated
public class FakeJob extends Job {

    private static final String TAG = FakeJob.class.getSimpleName();
    private final long mTimeInSeconds;

    public FakeJob(long timeInSeconds, Status finalStatus) {
        super(null, null, null);
        mTimeInSeconds = timeInSeconds;
    }

    @Override
    public void run() {
        Log.i(TAG, "Fake job start. Will take: " + mTimeInSeconds + " sec");
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(mTimeInSeconds));
        } catch (InterruptedException e) {
            Log.i(TAG, "Fake job was interrupted!!");
            e.printStackTrace();
        }
    }

}
