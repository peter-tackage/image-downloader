package com.moac.android.downloader.download;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.moac.android.downloader.service.DownloadService;

import java.util.HashMap;
import java.util.Map;
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

    public static final String DOWNLOAD_DISPATCHER_THREAD_NAME = "DownloadDispatcher";

    private final Context mContext;
    private final ExecutorService mRequestExecutor;
    private final DownloaderFactory mDownloaderFactory;
    private final Handler mDispatchHandler;
    private final HandlerThread mDispatchThread;

    // TODO This could be replaced by persistent storage
    private Map<String, Status> mStatusMap = new HashMap<String, Status>();

    public Scheduler(Context context, ExecutorService executor, DownloaderFactory downloaderFactory) {
        Log.i(TAG, "Creating scheduler");
        mContext = context;
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
                    case DownloadService.REQUEST_SUBMIT:
                        Request request = (Request)msg.obj;
                        Log.i(TAG, "Creating download job for id: " + request.getId());
                        handleStatusChanged(request, Status.PENDING);
                        Job job = new Job(request, mDownloaderFactory.newInstance(), Scheduler.this);
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
        mDispatchHandler.dispatchMessage(mDispatchHandler.obtainMessage(DownloadService.REQUEST_SUBMIT, request));
    }

    public void stop() {
        Log.i(TAG, "Shutting down Scheduler");
        mDispatchThread.quit();
        mRequestExecutor.shutdown();
    }

    @Override
    public void handleStatusChanged(Request request, Status status) {
        Log.i(TAG, "handleStatusChanged() Request: " + request.getUri() + " is now: " + status);
        Log.i(TAG, "handleStatusChanged() Thread: " + Thread.currentThread());
        Log.i(TAG, "handleStatusChanged() Scheduler: " + this);
        mStatusMap.put(request.getId(), status);
        Intent intent = new Intent(DownloadService.STATUS_EVENTS);
        intent.putExtra(DownloadService.DOWNLOAD_ID, request.getId());
        intent.putExtra(DownloadService.STATUS, status);
        mContext.sendBroadcast(intent);
    }

    public void cancel(String id) {
        // TODO
    }

    public Status getStatus(String id) {
        Log.i(TAG, "getStatus map: " + mStatusMap);
        Log.i(TAG, "getStatus map: " + mStatusMap.hashCode());
        Log.i(TAG, "getStatus Scheduler: " + this);
        Status status = mStatusMap.get(id);
        return status == null ? Status.UNKNOWN : status;
    }
}
