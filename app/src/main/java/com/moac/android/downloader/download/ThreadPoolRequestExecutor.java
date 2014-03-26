package com.moac.android.downloader.download;

import android.os.Handler;
import android.util.Log;

import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRequestExecutor implements RequestExecutor {

    private static final String TAG = ThreadPoolRequestExecutor.class.getSimpleName();

    private final ThreadPoolExecutor mRequestExecutor;
    private Handler mShutdownHandler;

    // Sensible defaults
    public ThreadPoolRequestExecutor() {
        this(5, 5, 0l, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public ThreadPoolRequestExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit timeUnit,
                                     BlockingQueue<Runnable> queue) {
        mRequestExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, timeUnit,
                queue) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                Log.i(TAG, "After Execute for " + r.toString());
                // FIXME This delay approach is bad.
                //
                // The problem is that during this method, the Executor still
                // threads this (non-main) thread as being active, so if the message
                //
                // is handled immediately, it may not trigger the shutdown.
                // The 500ms is roughly sufficient time for the Executor to remove
                // the Runnable from  its active list.
                //
                // At worst, the shutdown won't be triggered by the message handler,
                // which is better than shutting down prematurely. A service that stays
                // alive after the completion of its task is not desirable, but given that
                // it is idle, it will have negligible impact on the application.
                //
                mShutdownHandler.sendEmptyMessageDelayed(DownloadService.EXECUTION_COMPLETE, 500);
            }
        };
    }

    @Override
    public void submit(Job job) {
        mRequestExecutor.submit(job);
    }

    @Override
    public boolean isIdle() {
        return mRequestExecutor.getQueue().size() == 0 &&
                mRequestExecutor.getActiveCount() == 0;
    }

    @Override
    public void shutdown() {
        mRequestExecutor.shutdown();
    }

    @Override
    public void setOnPostExecuteHandler(Handler listener) {
        mShutdownHandler = listener;
    }

}
