package com.moac.android.downloader.test;

import android.test.AndroidTestCase;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.RequestStore;
import com.moac.android.downloader.download.Status;
import com.moac.android.downloader.download.StatusBarNotifier;
import com.moac.android.downloader.download.StatusHandler;
import com.moac.android.downloader.download.StatusNotifier;
import com.moac.android.downloader.download.Transitioner;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.api.Assertions.assertThat;
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
    public void tearDown() {
        mStatusNotifier = null;
        mStatusBarNotifier = null;
        mRequestStore = null;
    }

    // Status values are set to dummy values - Transitioner will allow all transitions
    public void test_notifiesWhenTransitionAllowed() {
        when(mTransitioner.isAllowed(any(Status.class),any(Status.class))).thenReturn(true);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);
        when(mRequestStore.getRequest(anyString())).thenReturn(dummyRequest());

        mStatusHandler.moveToStatus("dummy", Status.UNKNOWN);

        verify(mStatusNotifier).notifyStatus(any(Request.class));
        verify(mStatusBarNotifier).sendStatusBarNotification(any(Request.class));
    }

    // Status values are set to dummy values - Transitioner will disallow all transitions
    public void test_doesNotNotifyWhenTransitionDisallowed() {
        when(mTransitioner.isAllowed(any(Status.class), any(Status.class))).thenReturn(false);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);
        when(mRequestStore.getRequest(anyString())).thenReturn(dummyRequest());

        mStatusHandler.moveToStatus("dummy", Status.UNKNOWN);

        verify(mStatusNotifier, never()).notifyStatus(any(Request.class));
        verify(mStatusBarNotifier, never()).sendStatusBarNotification(any(Request.class));
    }

    public void test_updatesStateWhenTransitionAllowed() {
        when(mTransitioner.isAllowed(any(Status.class),any(Status.class))).thenReturn(true);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.UNKNOWN);
        Request request = dummyRequest();
        when(mRequestStore.getRequest(anyString())).thenReturn(request);

        mStatusHandler.moveToStatus("dummy", Status.RUNNING);

        assertThat(request.getStatus()).isEqualTo(Status.RUNNING);
    }

    // FIXME This test depends on the Request implementation's initial Status value
    public void test_doesNotUpdateStateWhenTransitionDisallowed() {
        when(mTransitioner.isAllowed(any(Status.class),any(Status.class))).thenReturn(false);
        when(mRequestStore.getStatus(anyString())).thenReturn(Status.CREATED);
        Request request = dummyRequest();
        when(mRequestStore.getRequest(anyString())).thenReturn(request);

        mStatusHandler.moveToStatus("dummy", Status.RUNNING);

        // State has not changed from that set in dummyRequest()
        assertThat(request.getStatus()).isEqualTo(Status.CREATED);
    }

    private static Request dummyRequest() {
        return new Request(null, null, null, null, null);
    }
}
