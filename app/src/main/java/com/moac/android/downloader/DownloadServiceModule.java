package com.moac.android.downloader;

import android.app.Service;
import android.content.Context;
import android.util.Log;

import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.HurlDownloader;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.injection.ForService;
import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Provides;

@dagger.Module(injects = {DownloadService.class, Downloader.class, HurlDownloader.class, RequestStore.class},
        addsTo = ApplicationModule.class, library = true)
public class DownloadServiceModule {
    private static final String TAG = DownloadServiceModule.class.getSimpleName();
    private final Service mService;

    public DownloadServiceModule(Service service) {
        mService = service;
    }

    @Provides
    @Singleton
    @ForService
    Context provideServiceContext() {
        return mService;
    }

    @Provides
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
        return new RequestStore();
    }

}
