package com.moac.android.downloader.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.moac.android.downloader.download.TestHelpers.dummyRequest;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class StatusBarNotifierTest extends AndroidTestCase {

    @Mock
    NotificationManager mNotificationManager;

    // SUT
    StatusBarNotifier mStatusBarNotifier;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Patch broken DexMaker
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        MockitoAnnotations.initMocks(this);
        mStatusBarNotifier = new StatusBarNotifier(getContext(), mNotificationManager);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mStatusBarNotifier = null;
        mNotificationManager = null;
    }

    @SmallTest
    public void test_cancelNotification() {
        Request r = dummyRequest(Status.CANCELLED);
        r.setNotificationId(1);

        mStatusBarNotifier.sendStatusBarNotification(r);

        verify(mNotificationManager).cancel(r.getNotificationId());
    }

    @SmallTest
    public void test_sendPendingNotification() {
        Request r = dummyRequest(Status.PENDING);
        r.setNotificationId(1);

        mStatusBarNotifier.sendStatusBarNotification(r);

        verify(mNotificationManager).notify(eq(r.getNotificationId()), any(Notification.class));
    }

    @SmallTest
    public void test_neverSendRunningNotification() {
        Request r = dummyRequest(Status.RUNNING);
        r.setNotificationId(1);

        mStatusBarNotifier.sendStatusBarNotification(r);
        // We don't send a specific notification for RUNNING
        verify(mNotificationManager, never()).notify(anyInt(), any(Notification.class));
    }
}

