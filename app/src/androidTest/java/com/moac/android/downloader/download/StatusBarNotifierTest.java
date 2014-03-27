package com.moac.android.downloader.download;

import android.app.NotificationManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.moac.android.downloader.download.TestHelpers.dummyRequest;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatusBarNotifierTest extends AndroidTestCase {

    @Mock
    NotificationManager mNotificationManager;

    // SUT
    private StatusBarNotifier mStatusBarNotifier;

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
    public void test_sendCancelledNotification() {
        Request r = dummyRequest(Status.CANCELLED);
        r.setNotificationId(1);

        mStatusBarNotifier.sendStatusBarNotification(r);

        verify(mNotificationManager).cancel(r.getNotificationId());
    }

}

