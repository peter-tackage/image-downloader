package com.moac.android.downloader.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.moac.android.downloader.service.DownloadService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestStore implements StatusHandler {

    private static final String TAG = RequestStore.class.getSimpleName();

    private final Context mContext;
    private Map<String, Request> mRequestMap = Collections.synchronizedMap(new HashMap<String, Request>());

    public RequestStore(Context context) {
        mContext = context;
    }

    public Status getStatus(String id) {
        Request request = mRequestMap.get(id);
        return request == null ? Status.UNKNOWN : request.getStatus();
    }

    public Request getRequest(String id) {
        return mRequestMap.get(id);
    }

    @Override
    public boolean moveToStatus(String id, Status toStatus) {
        // If can move return true, otherwise false.
        // Cannot transition from Cancelled, Successful or Failed
        Log.i(TAG, "Attempting to move id: " + id + " to: " + toStatus);
        Status currentStatus = getStatus(id);
        Log.i(TAG, "Current status of id: " + id + " to: " + currentStatus);
        switch (currentStatus) {
            case UNKNOWN:
            case CANCELLED:
            case SUCCESSFUL:
            case FAILED:
                return false;
            default:
                Request request = mRequestMap.get(id);
                request.setStatus(toStatus);
                notifyStateChanged(id, toStatus);
                return true;
        }
    }

    public Request create(String id, Uri uri, String destination) {
        Request request = new Request(id, uri, destination);
        request.setStatus(Status.CREATED);
        mRequestMap.put(id, request);
        Log.i(TAG, "Created Request has status:" + request.getStatus());
        return request;
    }

    private void notifyStateChanged(String id, Status status) {
        Intent intent = new Intent(DownloadService.STATUS_EVENTS);
        intent.putExtra(DownloadService.DOWNLOAD_ID, id);
        intent.putExtra(DownloadService.STATUS, status);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
