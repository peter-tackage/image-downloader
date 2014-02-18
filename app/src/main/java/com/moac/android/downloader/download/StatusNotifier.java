package com.moac.android.downloader.download;

/*
 * Interface for classes that notify of Job results
 *
 * ..
 */
public interface StatusNotifier {
    public void notifySuccess(String id, String resultFile);
    public void notifyStatus(String id, Status status);
}
