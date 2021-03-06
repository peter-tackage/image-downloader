package com.moac.android.downloader.download;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.moac.android.downloader.download.TestHelpers.dummyRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatusHandlerTest extends AndroidTestCase {

    @Mock
    StatusNotifier mStatusNotifier;
    @Mock
    StatusBarNotifier mStatusBarNotifier;
    @Mock
    RequestStore mRequestStore;
    @Mock
    Transitioner mTransitioner;

    // SUT
    private StatusHandler mStatusHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Patch broken DexMaker
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        MockitoAnnotations.initMocks(this);
        mStatusHandler = new StatusHandler(mTransitioner, mStatusNotifier, mStatusBarNotifier, mRequestStore);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mStatusNotifier = null;
        mStatusBarNotifier = null;
        mRequestStore = null;
        mTransitioner = null;
    }

    @SmallTest
    public void test_notifiesWhenTransitionAllowed() {
        // Status values are set to dummy values - Transitioner will allow all transitions
        when(mTransitioner.isAllowed(any(Status.class), any(Status.class))).thenReturn(true);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);
        when(mRequestStore.getRequest(anyString())).thenReturn(dummyRequest());

        mStatusHandler.moveToStatus("dummy", Status.UNKNOWN);

        verify(mStatusNotifier).notifyStatus(any(Request.class));
        verify(mStatusBarNotifier).sendStatusBarNotification(any(Request.class));
    }

    @SmallTest
    public void test_doesNotNotifyWhenTransitionDisallowed() {
        // Status values are set to dummy values - Transitioner will disallow all transitions
        when(mTransitioner.isAllowed(any(Status.class), any(Status.class))).thenReturn(false);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);
        when(mRequestStore.getRequest(anyString())).thenReturn(dummyRequest());

        mStatusHandler.moveToStatus("dummy", Status.UNKNOWN);

        verify(mStatusNotifier, never()).notifyStatus(any(Request.class));
        verify(mStatusBarNotifier, never()).sendStatusBarNotification(any(Request.class));
    }

    @SmallTest
    public void test_updatesStateWhenTransitionAllowed() {
        Request request = dummyRequest(Status.CREATED);
        when(mTransitioner.isAllowed(any(Status.class), any(Status.class))).thenReturn(true);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);
        when(mRequestStore.getRequest(anyString())).thenReturn(request);

        mStatusHandler.moveToStatus("dummy", Status.RUNNING);

        // State has been updated
        assertThat(request.getStatus()).isEqualTo(Status.RUNNING);
    }

    @SmallTest
    public void test_doesNotUpdateStateWhenTransitionDisallowed() {
        Request request = dummyRequest(Status.CREATED);
        when(mTransitioner.isAllowed(any(Status.class), any(Status.class))).thenReturn(false);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.CREATED);
        when(mRequestStore.getRequest(anyString())).thenReturn(request);

        mStatusHandler.moveToStatus("dummy", Status.RUNNING);

        // State has not changed from that set in dummyRequest()
        assertThat(request.getStatus()).isEqualTo(Status.CREATED);
    }
}

