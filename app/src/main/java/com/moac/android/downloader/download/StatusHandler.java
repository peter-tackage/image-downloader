package com.moac.android.downloader.download;

import android.util.Log;

/*
 * Responsible for state transition enforcement and notification
 */
public class StatusHandler {

    private static final String TAG = StatusHandler.class.getSimpleName();

    private final Transitioner mTransitioner;
    private final RequestStore mRequestStore;
    private final StatusBarNotifier mStatusBarNotifier;
    private final StatusNotifier mStatusNotifier;

    public StatusHandler(Transitioner transitioner, StatusNotifier statusNotifier,
                         StatusBarNotifier statusBarNotifier, RequestStore requestStore) {
        mTransitioner = transitioner;
        mStatusNotifier = statusNotifier;
        mStatusBarNotifier = statusBarNotifier;
        mRequestStore = requestStore;
    }

    /**
     * @param id       The id of the request
     * @param toStatus The desired status of request
     * @return Return true if the requested move is possible, false otherwise
     */
    public synchronized boolean moveToStatus(String id, Status toStatus) {
        Status currentStatus = mRequestStore.getStatus(id);
        Log.i(TAG, "moveToStatus() - Attempting to move id: " + id + " from: " + currentStatus + " => " + toStatus);

        boolean isMovePermitted = mTransitioner.isAllowed(currentStatus, toStatus);
        if (isMovePermitted) {
            setAndNotifyStateChanged(id, toStatus);
        }
        Log.i(TAG, "moveToStatus() - isAllowed: " + isMovePermitted);
        return isMovePermitted;
    }

    private void setAndNotifyStateChanged(String id, Status toStatus) {
        Request request = mRequestStore.getRequest(id);
        request.setStatus(toStatus);
        // Notify of status change and/or result
        if (mStatusNotifier != null) {
            mStatusNotifier.notifyStatus(request);
        }
        // Notify the status bar
        if (mStatusBarNotifier != null) {
            mStatusBarNotifier.sendStatusBarNotification(request);
        }
    }
}

