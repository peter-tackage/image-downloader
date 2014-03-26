package com.moac.android.downloader.download;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.moac.android.downloader.service.DownloadService;

/**
 * A {@link com.moac.android.downloader.download.StatusNotifier} that uses
 * a LocalBroadcastManager and Intents.
 * <p/>
 * The client is obviously dependent on this implementation if it wants to
 * receive status updates for the requests without binding to the Service.
 * <p/>
 */
public class LocalBroadcastStatusNotifier implements StatusNotifier {

    private final LocalBroadcastManager mLocalBroadcastManager;

    public LocalBroadcastStatusNotifier(LocalBroadcastManager localBroadcastManager) {
        mLocalBroadcastManager = localBroadcastManager;
    }

    @Override
    public void notifyStatus(Request request) {
        sendLocalBroadcastNotification(request);
    }

    private void sendLocalBroadcastNotification(Request request) {
        // The broadcast intent
        Status status = request.getStatus();
        Intent intent = new Intent(DownloadService.STATUS_EVENTS);
        intent.putExtra(DownloadService.DOWNLOAD_ID, request.getId());
        intent.putExtra(DownloadService.STATUS, request.getStatus());
        // Add the destination file path - it might be useful!
        if (status == Status.SUCCESSFUL) {
            intent.putExtra(DownloadService.LOCAL_LOCATION, request.getDestination());
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }

}
