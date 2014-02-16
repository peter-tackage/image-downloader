package com.moac.android.downloader.download;

interface StatusHandler {
    void handleStatusChanged(Request request, Status status);
}
