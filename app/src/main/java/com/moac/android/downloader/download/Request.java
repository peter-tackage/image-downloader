package com.moac.android.downloader.download;

import android.net.Uri;

/*
 * Represents a remote download request
 */
public class Request {

    private final String mId;
    private final String mDestination;
    private final Uri mUri;
    private Status mStatus;

    public Request(String id, Uri uri, String destination) {
        mId = id;
        mUri = uri;
        mDestination = destination;
        mStatus = Status.CREATED;
    }

    public String getId() { return mId; }
    public Uri getUri() {
        return mUri;
    }
    public String getDestination() {
        return mDestination;
    }
    public Status getStatus() { return mStatus;}
    void setStatus(Status status) { mStatus = status; }
    boolean isCancelled() { return mStatus == Status.CANCELLED; }
}
