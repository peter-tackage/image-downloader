package com.moac.android.downloader.download;

/*
 * A StatusNotifier is responsible for notifying interested parties
 * of Request status changes.
 *
 * TODO Could improve the API, it lacks symmetry.
 */
public interface StatusNotifier {
    public void notifySuccess(String id, String resultFile);

    public void notifyStatus(String id, Status status);
}
