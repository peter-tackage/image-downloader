package com.moac.android.downloader.service;

import android.net.Uri;

import com.moac.android.downloader.request.Request;

import java.util.EnumSet;

public interface DownloadClient {
    long download(Request request);

    void cancel(long id);

    long[] getRequests(EnumSet<Status> statuses);

}
