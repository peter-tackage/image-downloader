package com.moac.android.downloader;

import android.app.NotificationManager;
import android.content.Context;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.moac.android.downloader.download.DefaultTransitioner;
import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.HurlDownloader;
import com.moac.android.downloader.download.LocalBroadcastStatusNotifier;
import com.moac.android.downloader.download.RequestExecutor;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.StatusBarNotifier;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.download.StatusNotifier;
import com.moac.android.downloader.download.ThreadPoolRequestExecutor;
import com.moac.android.downloader.download.Transitioner;
import com.moac.android.downloader.injection.ForService;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadService;

import javax.inject.Singleton;

import dagger.Provides;

/**
 * Scoped to the lifetime of a Service instance - so singleton injections are re-provided
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
    RequestExecutor provideRequestExecutor() {
        return new ThreadPoolRequestExecutor();
    }

    @Provides
    @Singleton
    IBinder provideDownloadClient(RequestStore requestStore, StatusHandler statusHandler) {
        Log.i(TAG, "Providing IBinder");
        return new DefaultDownloadClient(requestStore, statusHandler);
    }

    @Provides
    @Singleton
    Transitioner provideTransitioner() {
        return new DefaultTransitioner();
    }

    @Provides
    @Singleton
    StatusHandler provideStatusHandler(Transitioner transitioner, StatusNotifier statusNotifier,StatusBarNotifier statusBarNotifier, RequestStore requestStore) {
        Log.i(TAG, "Providing StatusHandler");
        return new StatusHandler(transitioner, statusNotifier, statusBarNotifier, requestStore);
    }

    @Provides
    @Singleton
    StatusBarNotifier provideStatusBarUpdater(@ForService Context context) {
        return new StatusBarNotifier(context,
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    @Provides
    @Singleton
    StatusNotifier provideStatusNotifier(@ForService Context context) {
        Log.i(TAG, "Providing StatusNotifier");
        return new LocalBroadcastStatusNotifier(LocalBroadcastManager.getInstance(context));
    }

}
