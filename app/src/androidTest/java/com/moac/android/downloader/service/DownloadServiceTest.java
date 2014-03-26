package com.moac.android.downloader.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.moac.android.downloader.download.Job;
import com.moac.android.downloader.download.RequestExecutor;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.injection.Injector;
import com.moac.android.downloader.test.MockTestDownloaderApplication;

import javax.inject.Inject;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DownloadServiceTest extends ServiceTestCase<DownloadService> {

    @Inject
    RequestStore mRequestStore;

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

        // Provide injections into application for the service
        Application app = new MockTestDownloaderApplication();
        app.onCreate();
        setApplication(app);

        // Also provide mocks to this testcase
        ((Injector) (getApplication())).getObjectGraph().inject(this);
    }

    /**
     * Test basic startup
     */
    @SmallTest
    public void test_startable() {
        startService(getDummyDownloadIntent(getContext()));
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

    @MediumTest
    public void test_newDownloadRequest() {
        // Test the first new download -
        when(mStatusHandler.moveToStatus(anyString(), eq(Status.PENDING))).thenReturn(true);
        startService(getDummyDownloadIntent(getContext()));

        verify(mRequestExecutor).submit(any(Job.class));

    }

    // Provides a dummy download intent
    private static Intent getDummyDownloadIntent(Context context) {
        return getDummyIntent(context, "trackingId");
    }

    private static Intent getDummyIntent(Context context, String trackingId) {
        Intent startIntent = new Intent();
        startIntent.setClass(context, DownloadService.class);
        startIntent.putExtra(DownloadService.DOWNLOAD_ID, trackingId);
        startIntent.putExtra(DownloadService.REMOTE_LOCATION, "dummy://afile");
        startIntent.putExtra(DownloadService.LOCAL_LOCATION, "destinationFilename");
        startIntent.putExtra(DownloadService.DISPLAY_NAME, "displayName");
        return startIntent;
    }

}
