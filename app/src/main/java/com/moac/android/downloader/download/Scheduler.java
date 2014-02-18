package com.moac.android.downloader.download;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.ThreadPoolExecutor;

/*
 * Scheduler receives submitted download Request and
 * dispatches them to a pool of executors as a Runnable Job
 *
 * TODO Persistence and restart, status
 */
public class Scheduler {

    private static final String TAG = Scheduler.class.getSimpleName();
    private static final String DOWNLOAD_DISPATCHER_THREAD_NAME = "DownloadDispatcher";

    private final ThreadPoolExecutor mRequestExecutor;
    private final DownloaderFactory mDownloaderFactory;
    private final Handler mDispatchHandler;
    private final HandlerThread mDispatchThread;
    private final StatusHandler mStatusHandler;

    public Scheduler(StatusHandler statusHandler, ThreadPoolExecutor executor, DownloaderFactory downloaderFactory) {
        Log.i(TAG, "Creating Scheduler");
        mStatusHandler = statusHandler;
        mRequestExecutor = executor;
        mDownloaderFactory = downloaderFactory;
        mDispatchThread = new HandlerThread(DOWNLOAD_DISPATCHER_THREAD_NAME
                , android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mDispatchThread.start();
        mDispatchHandler = new Handler(mDispatchThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.i(TAG, "Handling msg on Dispatch Thread: " + Thread.currentThread());
                switch (msg.what) {
                    case DownloadService.REQUEST_SUBMIT:
                        Request request = (Request) msg.obj;
                        if (mStatusHandler.moveToStatus(request.getId(), Status.PENDING)) {
                            Log.i(TAG, "Creating download job for id: " + request.getId());
                            Job job = new Job(request, mDownloaderFactory.newInstance(), mStatusHandler);
                            mRequestExecutor.submit(job);
                        }
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
        mDispatchHandler.sendMessage(mDispatchHandler.obtainMessage(DownloadService.REQUEST_SUBMIT, request));
    }

    public void stop() {
        Log.i(TAG, "Shutting down Scheduler");
        mDispatchThread.quit();
        mRequestExecutor.shutdown();
    }

}
