package com.moac.android.downloader.test;

import android.os.IBinder;

import com.moac.android.downloader.download.DefaultTransitioner;
import com.moac.android.downloader.download.RequestExecutor;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.StatusBarNotifier;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.download.StatusNotifier;
import com.moac.android.downloader.download.Transitioner;
import com.moac.android.downloader.service.DefaultDownloadClient;
import com.moac.android.downloader.service.DownloadService;
import com.moac.android.downloader.service.DownloadServiceTest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Test module that appropriately mocks components for testing the core DownloadService
 */
@Module(overrides = true, injects = {MockDemoDownloaderApplication.class, DownloadService.class, DownloadServiceTest.class})
public class TestModule {

    @Provides
    @Singleton
    RequestStore provideRequestStore() {
       return new RequestStore();
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
    Transitioner provideTransitioner() {
        return new DefaultTransitioner();
    }

    @Provides
    @Singleton
    StatusHandler provideStatusHandler(Transitioner transitioner, RequestStore requestStore) {
        return new StatusHandler(transitioner, mock(StatusNotifier.class), mock(StatusBarNotifier.class), requestStore);
    }
}
