package com.moac.android.downloader;

import android.content.Context;
import android.util.Log;

import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.DownloaderFactory;
import com.moac.android.downloader.download.FakeDownloader;
import com.moac.android.downloader.download.HurlDownloader;
import com.moac.android.downloader.download.Scheduler;
import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.Executors;

import dagger.Provides;

@dagger.Module(injects = {DownloaderTestApplication.class, DownloadService.class})
public class FakeDownloadAppModule {
    private static final String TAG = FakeDownloadAppModule.class.getSimpleName();

    private final DownloaderTestApplication mApplication;

    public FakeDownloadAppModule(DownloaderTestApplication application) {
        mApplication = application;
    }

    @Provides
    Scheduler provideScheduler(Context applicationContext, DownloaderFactory factory) {
        Log.i(TAG, "Providing Scheduler");
        return new Scheduler(applicationContext, Executors.newFixedThreadPool(5), factory);
    }

    @Provides
    DownloaderFactory provideDownloaderFactory() {
        Log.i(TAG, "Providing DownloaderFactory");
        return new DownloaderFactory() {
            @Override
            public Downloader newInstance() {
                return new HurlDownloader();
            }
        };
    }

    @Provides
    Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }

}
