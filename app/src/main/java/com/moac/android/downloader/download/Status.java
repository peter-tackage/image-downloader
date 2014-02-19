package com.moac.android.downloader.download;

/**
 * Represents the possible states of the request
 * <p/>
 * Don't change the order without changing {@link StatusHandler#moveToStatus(String, Status)}
 */
public enum Status {
    UNKNOWN,
    CREATED,
    PENDING,
    RUNNING,
    CANCELLED,
    SUCCESSFUL,
    FAILED,
}
