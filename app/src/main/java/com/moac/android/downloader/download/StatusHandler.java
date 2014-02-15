package com.moac.android.downloader.download;

interface StatusHandler {
    void postStatusChanged(Request request, Status status);
}
