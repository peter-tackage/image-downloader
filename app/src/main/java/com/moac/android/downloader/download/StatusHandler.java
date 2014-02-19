package com.moac.android.downloader.download;

import android.util.Log;

/*
 * Responsible for state transition enforcement and notification
 */
public class StatusHandler {

    private static final String TAG = StatusHandler.class.getSimpleName();

    private final RequestStore mRequestStore;
    private StatusNotifier mStatusNotifier;

    public StatusHandler(StatusNotifier statusNotifier, RequestStore requestStore) {
        mStatusNotifier = statusNotifier;
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

        boolean isMovePermitted = isMovePermitted(id, toStatus);
        if (isMovePermitted) {
            setAndNotifyStateChanged(id, toStatus);

        }
        Log.i(TAG, "moveToStatus() - isMoveAllowed: " + isMovePermitted);
        return isMovePermitted;
    }

    private boolean isMovePermitted(String id, Status toStatus) {
        Status currentStatus = mRequestStore.getStatus(id);
        boolean isMovePermitted;
        switch (currentStatus) {
            case UNKNOWN:
            case CANCELLED:
            case SUCCESSFUL:
            case FAILED:
                // Only support a restart from a finished state
                isMovePermitted = toStatus == Status.CREATED || toStatus == Status.PENDING
                        || currentStatus == toStatus;
                break;
            case CREATED:
            case PENDING:
            case RUNNING:
                // Don't support move back for an in-progress request
                isMovePermitted = toStatus.ordinal() > currentStatus.ordinal();
                break;
            default:
                throw new IllegalStateException("Unhandled request state: " + currentStatus);
        }
        return isMovePermitted;
    }

    private void setAndNotifyStateChanged(String id, Status toStatus) {
        Request request = mRequestStore.getRequest(id);
        request.setStatus(toStatus);
        // Notify of status change and/or result
        if (mStatusNotifier != null) {
            mStatusNotifier.notifyStatus(request);
        }
    }
}

