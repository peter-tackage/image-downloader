package com.moac.android.downloader;

import android.util.Log;

import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.DownloaderFactory;
import com.moac.android.downloader.download.HurlDownloader;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.service.DownloadService;

import java.util.concurrent.ExecutorService;
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
    ExecutorService provideExecutor() {
        Log.i(TAG, "Providing ExecutorService");
        return Executors.newFixedThreadPool(5);
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
    RequestStore provideRequestStore() {
        Log.i(TAG, "Providing RequestStore");
        return new RequestStore(mApplication.getApplicationContext());
    }

}
