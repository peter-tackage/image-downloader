package com.moac.android.downloader.download;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.moac.android.downloader.service.DownloadService;

public class StatusHandler {

    private final Context mContext;
    private final RequestStore mRequestStore;

    public StatusHandler(Context context, RequestStore requestStore) {
        mContext = context;
        mRequestStore = requestStore;
    }

    private static final String TAG = StatusHandler.class.getSimpleName();

    public boolean moveToStatus(String id, Status toStatus) {
        // If can move return true, otherwise false.
        // Cannot transition from Cancelled, Successful or Failed
        Log.i(TAG, "Attempting to move id: " + id + " to: " + toStatus);
        Status currentStatus = mRequestStore.getStatus(id);
        Log.i(TAG, "Current status of id: " + id + " to: " + currentStatus);
        switch (currentStatus) {
            case UNKNOWN:
            case CANCELLED:
            case SUCCESSFUL:
            case FAILED:
                return false;
            default:
                Request request = mRequestStore.getRequest(id);
                request.setStatus(toStatus);
                notifyStateChanged(id, toStatus);
                return true;
        }
    }

    private void notifyStateChanged(String id, Status status) {
        Intent intent = new Intent(DownloadService.STATUS_EVENTS);
        intent.putExtra(DownloadService.DOWNLOAD_ID, id);
        intent.putExtra(DownloadService.STATUS, status);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
