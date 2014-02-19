package com.moac.android.downloader.download;

import android.net.Uri;

/*
 * Represents a remote download request
 */
public class Request {

    public static final int UNSET_NOTIFICATION_ID = -1;

    private final String mId;
    private final String mDisplayName;
    private final String mDestination;
    private final Uri mUri;
    private Status mStatus;
    private final String mMediaType;
    private int mNotificationId;

    public Request(String id, String name, Uri uri, String destination, String mediaType) {
        mId = id;
        mDisplayName = name;
        mUri = uri;
        mDestination = destination;
        mStatus = Status.CREATED;
        mMediaType = mediaType;
        mNotificationId = UNSET_NOTIFICATION_ID;
    }

    public String getId() {
        return mId;
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

    void setStatus(Status status) {
        mStatus = status;
    }

    public String getDisplayName() {return mDisplayName; }

    public int getNotificationId() { return mNotificationId; }

    public void setNotificationId(int notificationId) { mNotificationId = notificationId; };

    public String getMediaType() { return mMediaType; }

    boolean isCancelled() {
        return mStatus == Status.CANCELLED;
    }
}
