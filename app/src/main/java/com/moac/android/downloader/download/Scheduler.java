package com.moac.android.downloader.download;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/*
 * Scheduler receives submitted download Request and
 * dispatches them to a pool of executors as a Runnable Job
 *
 * TODO Persistence and restart, status
 */
public class Scheduler {

    private static final String TAG = Scheduler.class.getSimpleName();
    private static final int REQUEST_SUBMIT = 1;
    public static final String DOWNLOAD_DISPATCHER_THREAD_NAME = "DownloadDispatcher";

    private final ExecutorService mRequestExecutor;
    private final Handler mDispatchHandler;
    private final HandlerThread mDispatchThread;

    @Inject
    public Scheduler(ExecutorService executor, final DownloaderFactory factory) {
        mRequestExecutor = executor;
        mDispatchThread = new HandlerThread(DOWNLOAD_DISPATCHER_THREAD_NAME
                , android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mDispatchThread.start();
        mDispatchHandler = new Handler(mDispatchThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.i(TAG, "Handling msg");
                switch (msg.what) {
                    case REQUEST_SUBMIT:
                        Log.i(TAG, "Creating download job");
                        Job job = new Job((Request) msg.obj, factory.newInstance());
                        // TODO Do something with the future
                        mRequestExecutor.submit(job);
                        break;
                    default:
                        Log.e(TAG, "Unexpected message type handled: " + msg.what);
                        break;
                }
                return true;
            }
        });
    }

    public void submit(Request request) {
        mDispatchHandler.dispatchMessage(mDispatchHandler.obtainMessage(REQUEST_SUBMIT, request));
    }

    public void stop() {
        mDispatchThread.quit();
        mRequestExecutor.shutdown();
    }
}
