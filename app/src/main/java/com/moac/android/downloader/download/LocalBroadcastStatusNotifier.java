package com.moac.android.downloader.download;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.moac.android.downloader.service.DownloadService;

/**
 * A {@link com.moac.android.downloader.download.StatusNotifier} that implements
 * the StatusNotifier using a LocalBroadcastManager and Intents.
 * The client is obviously dependent on this implementation if it wants to
 * receive status updates for the requests without binding to the Service.
 */
public class LocalBroadcastStatusNotifier implements StatusNotifier {

    private final LocalBroadcastManager mLocalBroadcastManager;

    public LocalBroadcastStatusNotifier(LocalBroadcastManager localBroadcastManager) {
        mLocalBroadcastManager = localBroadcastManager;
    }

    @Override
    public void notifySuccess(String id, String resultFile) {
       notify(id, Status.SUCCESSFUL, resultFile);
    }

    @Override
    public void notifyStatus(String id, Status status) {
        notify(id, status, null);
    }

    private void notify(String id, Status status, String resultFile) {
        Intent intent = new Intent(DownloadService.STATUS_EVENTS);
        intent.putExtra(DownloadService.DOWNLOAD_ID, id);
        intent.putExtra(DownloadService.STATUS, status);
        if(status == Status.SUCCESSFUL && resultFile != null) {
            intent.putExtra(DownloadService.LOCAL_LOCATION, resultFile);
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }

}
