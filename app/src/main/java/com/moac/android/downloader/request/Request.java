package com.moac.android.downloader.request;

import android.net.Uri;

import com.moac.android.downloader.service.Status;

/*
 * Describes a remote download request from perspective of a client
 *
 * TODO Add the ability to set HTTP header for authorized requests
 */
public class Request {

    private final String mDestination;
    private final Uri mUri;
    private Status mStatus;

    public Request(Uri uri, String destination) {
        mUri = uri;
        mDestination = destination;
    }

    public Uri getUri() {
        return mUri;
    }

    public String getDestination() {
        return mDestination;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

}
