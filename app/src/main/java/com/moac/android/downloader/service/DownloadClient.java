package com.moac.android.downloader.service;

import android.net.Uri;

import com.moac.android.downloader.download.Status;

public interface DownloadClient {
    void cancel(String id);
    Status getStatus(String id);
}
