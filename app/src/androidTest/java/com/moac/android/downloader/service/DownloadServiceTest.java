package com.moac.android.downloader.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.moac.android.downloader.download.Job;
import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.RequestExecutor;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.TestHelpers;
import com.moac.android.downloader.injection.Injector;
import com.moac.android.downloader.test.MockDemoDownloaderApplication;

import javax.inject.Inject;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class DownloadServiceTest extends ServiceTestCase<DownloadService> {

    @Inject
    RequestStore mRequestStore;

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
        Application app = new MockDemoDownloaderApplication();
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

        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), DownloadService.class);
        IBinder service = bindService(startIntent);
        assertThat(service instanceof DownloadClient).isTrue();

        DownloadClient client = ((DownloadClient)service);

        assertThat(client.getStatus("dummy")).isEqualTo(Status.UNKNOWN);
    }

    @MediumTest
    public void test_newDownloadRequest() {
        // Test the first new download -
        startService(getDummyDownloadIntent(getContext(), "trackingId"));

        // Verify that is submitted to the executor
        verify(mRequestExecutor).submit(any(Job.class));

        // Verify request is known by the RequestStore
        assertThat(mRequestStore.getStatus("trackingId")).isNotEqualTo(Status.UNKNOWN);
    }

    @MediumTest
    public void test_rejectRunning() {
        // Add an existing RUNNING request to the store
        Request runningRequest = TestHelpers.dummyRequest(Status.RUNNING);
        mRequestStore.add(runningRequest);

        Intent duplicateDownloadIntent = getDummyDownloadIntent(getContext(), runningRequest.getId());
        startService(duplicateDownloadIntent);

        // Verify that no request has been submitted to the executor
        verify(mRequestExecutor, never()).submit(any(Job.class));
    }

    // Provides a dummy download intent
    private static Intent getDummyDownloadIntent(Context context) {
        return getDummyDownloadIntent(context, "trackingId");
    }

    private static Intent getDummyDownloadIntent(Context context, String trackingId) {
        Intent startIntent = new Intent();
        startIntent.setClass(context, DownloadService.class);
        startIntent.putExtra(DownloadService.DOWNLOAD_ID, trackingId);
        startIntent.putExtra(DownloadService.REMOTE_LOCATION, "dummy://afile");
        startIntent.putExtra(DownloadService.LOCAL_LOCATION, "destinationFilename");
        startIntent.putExtra(DownloadService.DISPLAY_NAME, "displayName");
        return startIntent;
    }

}
