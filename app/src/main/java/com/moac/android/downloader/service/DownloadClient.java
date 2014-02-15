package com.moac.android.downloader.service;

import com.moac.android.downloader.download.Request;
import com.moac.android.downloader.download.Status;

import java.util.EnumSet;

public interface DownloadClient {
    long download(Request request);

    void cancel(long id);

    long[] getRequests(EnumSet<Status> statuses);

    void addListener(DownloadListener listener);
    void removeListener(DownloadListener listener);

    public interface DownloadListener {
        public void onDownloadEvent(Request request);
    }

}
