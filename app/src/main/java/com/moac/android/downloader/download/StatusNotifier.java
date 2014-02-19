package com.moac.android.downloader.download;

/*
 * A StatusNotifier is responsible for notifying interested parties
 * of Request status changes.
 */
public interface StatusNotifier {
    void notifyStatus(Request request);
}
