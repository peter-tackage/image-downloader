package com.moac.android.downloader;

import android.app.Service;
import android.util.Log;

import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.HurlDownloader;
import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dagger.Provides;

@dagger.Module(injects = {DownloadService.class, Downloader.class, HurlDownloader.class},
        addsTo = ApplicationModule.class)
public class DownloadServiceModule {
    private static final String TAG = DownloadServiceModule.class.getSimpleName();
    private final Service mService;

    public DownloadServiceModule(Service service) {
        mService = service;
    }

    @Provides
    ThreadPoolExecutor provideThreadPoolExecutor() {
        Log.i(TAG, "Providing ThreadPoolExecutor");
        return new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @Provides
    Downloader provideDownloader(HurlDownloader downloader) {
        Log.i(TAG, "Providing Downloader");
        return downloader;
    }

}
