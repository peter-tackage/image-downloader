package com.moac.android.downloader.download;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/*
 * TODO Persistence and restart, status
 */
public class RequestScheduler {

    private static final String TAG = RequestScheduler.class.getSimpleName();
    private static final int REQUEST_SUBMIT = 1;

    // Executor of Requests
    private final ExecutorService mRequestExecutor;
    private final Handler mDispatchHandler;
    private final HandlerThread mDispatchThread;

    // Takes requests, queues them uses executor to determine
    // when to unqueue and submit
    @Inject
    public RequestScheduler(ExecutorService executor, final DownloaderFactory factory) {
        mRequestExecutor = executor;
        // Create dispatch thread from background thread
        mDispatchThread = new HandlerThread("DownloadDispatcher"
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

    public void start() {
        // Start the executor
        // Start the dispatcher
        // TODO Um.... already started...
    }

    public void stop() {
        // Stop the dispatcher
        // Stop the executor
        mDispatchThread.quit();
        mRequestExecutor.shutdown();
    }
}
