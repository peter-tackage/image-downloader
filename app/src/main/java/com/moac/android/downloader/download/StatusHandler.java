package com.moac.android.downloader.download;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.moac.android.downloader.service.DownloadService;

public class StatusHandler {

    private static final String TAG = StatusHandler.class.getSimpleName();

    private final LocalBroadcastManager mLocalBroadCastManager;
    private final RequestStore mRequestStore;

    public StatusHandler(LocalBroadcastManager localBroadcastManager, RequestStore requestStore) {
        mLocalBroadCastManager = localBroadcastManager;
        mRequestStore = requestStore;
    }

    // Return true if the request support move is possible
    public boolean moveToStatus(String id, Status toStatus) {
        Status currentStatus = mRequestStore.getStatus(id);
        Log.i(TAG, "moveToStatus() - Attempting to move id: " + id + " from: " + currentStatus + " => " + toStatus);
        boolean isMoveAllowed;
        switch (currentStatus) {
            case UNKNOWN:
            case CANCELLED:
            case SUCCESSFUL:
            case FAILED:
                // Only support a restart from a finished state
                isMoveAllowed = toStatus == Status.CREATED;
                break;
            case CREATED:
            case PENDING:
            case RUNNING:
                // Don't support move back for an in-progress request
                isMoveAllowed = toStatus.ordinal() > currentStatus.ordinal();
                break;
            default:
                throw new IllegalStateException("Unhandled request state: " + currentStatus);
        }
        if (isMoveAllowed) {
            setAndNotifyStateChanged(id, toStatus);
        }
        Log.i(TAG, "moveToStatus() - isMoveAllowed: " + isMoveAllowed);
        return isMoveAllowed;
    }

    private void setAndNotifyStateChanged(String id, Status toStatus) {
        Request request = mRequestStore.getRequest(id);
        request.setStatus(toStatus);
        // Notify via Localbroadcast
        if (mLocalBroadCastManager != null) {
            Intent intent = new Intent(DownloadService.STATUS_EVENTS);
            intent.putExtra(DownloadService.DOWNLOAD_ID, id);
            intent.putExtra(DownloadService.STATUS, toStatus);
            if(toStatus == Status.SUCCESSFUL) {
                intent.putExtra(DownloadService.LOCAL_LOCATION, mRequestStore.getRequest(id).getDestination());
            }
            mLocalBroadCastManager.sendBroadcast(intent);
        }
    }
}
