package com.moac.android.downloader;

import android.app.NotificationManager;
import android.content.Context;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.HurlDownloader;
import com.moac.android.downloader.download.LocalBroadcastStatusNotifier;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.download.StatusNotifier;
import com.moac.android.downloader.injection.ForService;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Provides;

/**
 * Scoped to the lifetime of a Service - so singleton injections are re-provided
 * when another Service instance is created.
 */
@dagger.Module(injects = {DownloadService.class, Downloader.class, HurlDownloader.class, RequestStore.class},
        addsTo = ApplicationModule.class)
public class DownloadServiceModule {
    private static final String TAG = DownloadServiceModule.class.getSimpleName();
    private final DownloadService mService;

    public DownloadServiceModule(DownloadService service) {
        mService = service;
    }

    @Provides
    @Singleton
    @ForService
    Context provideServiceContext() {
        Log.i(TAG, "Providing Service Context");
        return mService;
    }

    @Provides
    @Singleton
    ThreadPoolExecutor provideThreadPoolExecutor() {
        Log.i(TAG, "Providing ThreadPoolExecutor");
        return new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @Provides
    Downloader provideDownloader() {
        Log.i(TAG, "Providing Downloader");
        return new HurlDownloader();
    }

    @Provides
    @Singleton
    RequestStore provideRequestStore() {
        Log.i(TAG, "Providing RequestStore");
        return new RequestStore();
    }

    @Provides
    @Singleton
    IBinder provideDownloadClient(RequestStore requestStore, StatusHandler statusHandler) {
        Log.i(TAG, "Providing IBinder");
        return new DefaultDownloadClient(requestStore, statusHandler);
    }

    @Provides
    @Singleton
    StatusHandler provideStatusHandler(StatusNotifier statusNotifier, RequestStore requestStore) {
        Log.i(TAG, "Providing StatusHandler");
        return new StatusHandler(statusNotifier, requestStore);
    }

    @Provides
    @Singleton
    StatusNotifier provideStatusNotifier(@ForService Context context) {
        Log.i(TAG, "Providing StatusNotifier");
        return new LocalBroadcastStatusNotifier(context,
                LocalBroadcastManager.getInstance(context),
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }

}
