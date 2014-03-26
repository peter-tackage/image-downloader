package com.moac.android.downloader.download;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.moac.android.downloader.service.DownloadService;

import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

public class LocalBroadcastStatusNotifierTest extends AndroidTestCase {

    @Mock
    LocalBroadcastManager mLocalBroadcastManager;

    // SUT
    private LocalBroadcastStatusNotifier mStatusNotifer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Patch broken DexMaker
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        MockitoAnnotations.initMocks(this);
        mStatusNotifer = new LocalBroadcastStatusNotifier(mLocalBroadcastManager);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mStatusNotifer = null;
    }

    // Verify the LBM is given the correct Intent
    @SmallTest
    public void test_successfulIntent() {
        Request request = new Request("id", "name", Uri.EMPTY, "destination", "mediaType");
        request.setStatus(Status.SUCCESSFUL);

        mStatusNotifer.notifyStatus(request);

        verify(mLocalBroadcastManager).sendBroadcast(argThat(new IsExpectedIntent(request.getId(),
                request.getStatus(),request.getDestination())));
    }

    // Verify that the LOCAL_LOCATION is not set
    @SmallTest
    public void test_unSuccessfulIntent() {
        Request request = new Request("id", "name", Uri.EMPTY, "destination", "mediaType");
        request.setStatus(Status.FAILED);

        mStatusNotifer.notifyStatus(request);

        verify(mLocalBroadcastManager).sendBroadcast(argThat(new IsExpectedIntent(request.getId(),
                request.getStatus(),request.getDestination())));
    }

    class IsExpectedIntent extends ArgumentMatcher<Intent> {
        private final String mDestination;
        private final String mId;
        private final Status mStatus;

        public IsExpectedIntent(String id, Status status, String destination) {
            mId = id;
            mStatus = status;
            mDestination = destination;
        }
        public boolean matches(Object obj) {
            if (obj == null || !(obj instanceof  Intent)) return false;

            Intent intent = (Intent)obj;
            return intent.getAction().equals(DownloadService.STATUS_EVENTS)
                    && intent.getStringExtra(DownloadService.DOWNLOAD_ID).equals(mId)
                    && intent.getExtras().get(DownloadService.STATUS).equals(mStatus)
                    && mStatus == Status.SUCCESSFUL ?
                    intent.getStringExtra(DownloadService.LOCAL_LOCATION).equals(mDestination) :
                    intent.getStringExtra(DownloadService.LOCAL_LOCATION) == null;
        }

    }

}
