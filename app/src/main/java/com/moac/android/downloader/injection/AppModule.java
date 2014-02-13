package com.moac.android.downloader.injection;

import android.os.IBinder;
import android.util.Log;

import com.moac.android.downloader.DownloaderTestApplication;
import com.moac.android.downloader.request.Downloader;
import com.moac.android.downloader.request.DownloaderFactory;
import com.moac.android.downloader.request.FakeDownloader;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadService;
import com.moac.android.downloader.service.RequestScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Provides;

@dagger.Module(injects = {DownloaderTestApplication.class, DownloadService.class})
public class AppModule {
    private static final String TAG = AppModule.class.getSimpleName();

    private final DownloaderTestApplication mApplication;

    public AppModule(DownloaderTestApplication application) {
        mApplication = application;
    }

    @Provides
    IBinder provideDownloadClient(RequestScheduler requestScheduler) {
        Log.i(TAG, "Providing download client binder");
        return new DefaultDownloadClient(requestScheduler);
    }

    @Provides
    ExecutorService provideRequestExecutor() {
        Log.i(TAG, "Providing provideRequestExecutor");
        // TODO Configure the queue size
        return Executors.newFixedThreadPool(5);
    }

    @Provides
    RequestScheduler provideRequestScheduler(ExecutorService requestExecutor, DownloaderFactory factory) {
        return new RequestScheduler(requestExecutor, factory);
    }

    @Provides
    DownloaderFactory provideDownloadFactory() {
        return new DownloaderFactory() {
            @Override
            public Downloader newInstance() {
                return new FakeDownloader(10);
            }
        };
    }

}
