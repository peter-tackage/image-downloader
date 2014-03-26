package com.moac.android.downloader.test;

import android.os.IBinder;

import com.moac.android.downloader.download.Downloader;
import com.moac.android.downloader.download.RequestExecutor;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadService;
import com.moac.android.downloader.service.DownloadServiceTest;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module(injects = {MockTestDownloaderApplication.class, DownloadService.class, DownloadServiceTest.class})
public class TestModule {

    @Provides
    @Singleton
    RequestStore provideRequestStore() {
       return mock(RequestStore.class);
    }

    @Provides
    @Singleton
    RequestExecutor provideRequestExecutor() {
        return mock(RequestExecutor.class);
    }

    @Provides
    @Singleton
    IBinder provideDownloadClient(RequestStore requestStore, StatusHandler statusHandler) {
        return new DefaultDownloadClient(requestStore, statusHandler);
    }

    @Provides
    @Singleton
    StatusHandler provideStatusHandler() {
        return mock(StatusHandler.class);
    }
}
