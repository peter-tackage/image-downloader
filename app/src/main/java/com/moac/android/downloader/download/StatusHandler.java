package com.moac.android.downloader.download;

interface StatusHandler {
    boolean moveToStatus(String id, Status status);
}
