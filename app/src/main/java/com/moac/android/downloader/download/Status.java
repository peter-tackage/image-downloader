package com.moac.android.downloader.download;

/**
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
