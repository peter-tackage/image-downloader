package com.moac.android.downloader.download;

import java.io.IOException;

/*
 * Represents a HTTP networking exception
 */
public class ResponseException extends IOException {
    int mResponseCode;
    String mMessage;

    public ResponseException(int responseCode, String message) {
        mResponseCode = responseCode;
        mMessage = message;
    }
}
