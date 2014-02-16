package com.moac.android.downloader.download;

import java.io.InputStream;

public class NetworkResponse {
    private InputStream mInputStream;
    private long mContentLength;

    public NetworkResponse(InputStream mInputStream, long contentLength) {
        this.mInputStream = mInputStream;
        mContentLength = contentLength;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public long getContentLength() {
        return mContentLength;
    }
}
