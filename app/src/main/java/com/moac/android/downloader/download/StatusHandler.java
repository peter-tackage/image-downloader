package com.moac.android.downloader.download;

import android.util.Log;

/*
 * Responsible for state transition enforcement and notification
 *
 */
public class StatusHandler {

    private static final String TAG = StatusHandler.class.getSimpleName();

    private final RequestStore mRequestStore;
    private StatusNotifier mStatusNotifier;

    public StatusHandler(StatusNotifier statusNotifier, RequestStore requestStore) {
        mStatusNotifier = statusNotifier;
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
                isMoveAllowed = toStatus == Status.CREATED || toStatus == Status.PENDING
                        || currentStatus == toStatus;
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
        // Notify of status change and/or result
        if (mStatusNotifier != null) {
            if (toStatus == Status.SUCCESSFUL) {
                mStatusNotifier.notifySuccess(id, request.getDestination());
            } else {
                mStatusNotifier.notifyStatus(id, toStatus);
            }
        }
    }
}
