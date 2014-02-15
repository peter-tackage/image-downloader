package com.moac.android.downloader.download;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.moac.android.downloader.service.DownloadClient;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/*
 * Scheduler receives submitted download Request and
 * dispatches them to a pool of executors as a Runnable Job
 *
 * TODO Persistence and restart, status
 * TODO Prevent duplicate download attempts
 */
public class Scheduler implements StatusHandler {

    private static final String TAG = Scheduler.class.getSimpleName();
    private static final int REQUEST_SUBMIT = 1;
    public static final String DOWNLOAD_DISPATCHER_THREAD_NAME = "DownloadDispatcher";

    private final ExecutorService mRequestExecutor;
    private final DownloaderFactory mDownloaderFactory;
    private final Handler mDispatchHandler;
    private final HandlerThread mDispatchThread;
    private Set<DownloadClient.DownloadListener> mListeners;

    @Inject
    public Scheduler(ExecutorService executor, DownloaderFactory downloaderFactory) {
        Log.i(TAG, "Creating scheduler");
        mRequestExecutor = executor;
        mDownloaderFactory = downloaderFactory;
        mDispatchThread = new HandlerThread(DOWNLOAD_DISPATCHER_THREAD_NAME
                , android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mDispatchThread.start();
        mDispatchHandler = new Handler(mDispatchThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.i(TAG, "Handling msg on Thread: " + Thread.currentThread());
                switch (msg.what) {
                    case REQUEST_SUBMIT:
                        Log.i(TAG, "Creating download job");
                        Job job = new Job((Request) msg.obj, mDownloaderFactory.newInstance(), Scheduler.this);
                        mRequestExecutor.submit(job);
                        break;
                    default:
                        Log.e(TAG, "Unexpected message type handled: " + msg.what);
                        break;
                }
                return true;
            }
        });
        mListeners = new HashSet<DownloadClient.DownloadListener>();
    }

    public void submit(Request request) {
        mDispatchHandler.dispatchMessage(mDispatchHandler.obtainMessage(REQUEST_SUBMIT, request));
    }

    public void stop() {
        Log.i(TAG, "Shutting down Scheduler");
        mDispatchThread.quit();
        mRequestExecutor.shutdown();
    }

    public void addEventListener(DownloadClient.DownloadListener listener) {
        mListeners.add(listener);
        Log.i(TAG, "Added listener, size: " + mListeners.size());
    }

    public void removeEventListener(DownloadClient.DownloadListener listener) {
        mListeners.remove(listener);
        Log.i(TAG, "Removed listener, size: " + mListeners.size());
    }

    public void onRequestEvent(Request request) {
        // TODO Could persist here too
        for(DownloadClient.DownloadListener listener : mListeners) {
            listener.onDownloadEvent(request);
        }
    }

    @Override
    public void postStatusChanged(Request request, Status status) {
        request.setStatus(status);
        Log.i(TAG, "postStatusChanged() Request: " + request.getUri() + " is now: " + status);
        Log.i(TAG, "postStatusChanged() Thread: " + Thread.currentThread());
      //  notifyRequestEvent(request);
    }
}
