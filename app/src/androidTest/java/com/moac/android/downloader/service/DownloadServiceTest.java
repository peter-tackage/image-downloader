package com.moac.android.downloader.service;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.moac.android.downloader.download.RequestExecutor;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.injection.Injector;
import com.moac.android.downloader.test.MockTestDownloaderApplication;
import com.moac.android.downloader.test.TestModule;

import java.io.File;

import javax.inject.Inject;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class DownloadServiceTest extends ServiceTestCase<DownloadService> {

    @Inject
    RequestStore mRequestStore;

    @Inject
    IBinder mDownloadClient;

    @Inject
    StatusHandler mStatusHandler;

    @Inject
    RequestExecutor mRequestExecutor;

    public DownloadServiceTest() {
        super(DownloadService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Patch broken DexMaker
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());

        // Provide injections
        Application app = new MockTestDownloaderApplication();
        app.onCreate();
        setApplication(app);

        // Add injections of mocks
        ((Injector) (getApplication())).getObjectGraph().inject(this);
    }

    /**
     * Test basic startup
     */
    @SmallTest
    public void test_startable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), DownloadService.class);
        startIntent.putExtra(DownloadService.DOWNLOAD_ID, "trackingId");
                startIntent.putExtra(DownloadService.REMOTE_LOCATION, "dummy://afile");
        startIntent.putExtra(DownloadService.LOCAL_LOCATION, "destinationFilename");
        startIntent.putExtra(DownloadService.DISPLAY_NAME, "displayName");
        startService(startIntent);
    }

    /**
     * Test binding to service
     */
    @SmallTest
    public void test_bindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), DownloadService.class);
        IBinder service = bindService(startIntent);
        assertThat(service instanceof DownloadClient).isTrue();
    }

    @SmallTest
    public void test_idleDownloadClient() {
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);

        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), DownloadService.class);
        IBinder service = bindService(startIntent);
        assertThat(service instanceof DownloadClient).isTrue();

        DownloadClient client = ((DownloadClient)service);

        assertThat(client.getStatus(anyString())).isEqualTo(Status.UNKNOWN);
    }

}
