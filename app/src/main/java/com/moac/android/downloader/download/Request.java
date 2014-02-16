package com.moac.android.downloader.download;

import android.net.Uri;

/*
 * Describes a remote download request
 */
public class Request {

    private final String mId;
    private final String mDestination;
    private final Uri mUri;

    public Request(String id, Uri uri, String destination) {
        mId = id;
        mUri = uri;
        mDestination = destination;
    }

    public String getId() { return mId; }
    public Uri getUri() {
        return mUri;
    }

    public String getDestination() {
        return mDestination;
    }

}
