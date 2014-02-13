package com.moac.android.downloader.injection;

import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.DownloaderTestApplication;
import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.DownloaderFactory;
import com.moac.android.downloader.download.FakeDownloader;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadService;
import com.moac.android.downloader.download.RequestScheduler;

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
    IBinder provideDownloadClient(RequestScheduler requestScheduler) {
        Log.i(TAG, "Providing download client binder");
        return new DefaultDownloadClient(requestScheduler);
    }

    @Provides
    ExecutorService provideExecutorService() {
        Log.i(TAG, "Providing ExecutorService");
        return Executors.newFixedThreadPool(5);
    }

    @Provides
    RequestScheduler provideRequestScheduler(ExecutorService requestExecutor, DownloaderFactory factory) {
        Log.i(TAG, "Providing RequestScheduler");
        return new RequestScheduler(requestExecutor, factory);
    }

    @Provides
    DownloaderFactory provideDownloaderFactory() {
        Log.i(TAG, "Providing DownloaderFactory");
        return new DownloaderFactory() {
            @Override
            public Downloader newInstance() {
                return new FakeDownloader(10);
            }
        };
    }

}
