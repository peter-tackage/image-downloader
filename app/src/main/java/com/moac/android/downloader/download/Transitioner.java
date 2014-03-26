package com.moac.android.downloader.download;

public interface Transitioner {
    public boolean isAllowed(Status from, Status to);
}
