package com.moac.android.downloader.service;

import com.moac.android.downloader.download.Status;

import java.util.EnumSet;

public interface DownloadClient {
    void cancel(String id);
    Status getStatus(String id);
    String generateNextId();
}
