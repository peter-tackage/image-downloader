package com.moac.android.downloader.download;

import android.net.Uri;

public class TestHelpers {

    public static final String TRACKING_ID = "trackingId";
    public static final String NAME = "name";
    public static final Uri URI = Uri.parse("file://destination");
    public static final String DESTINATION = "destination";
    public static final String MEDIA_TYPE = "mediaType";

    public static Request dummyRequest() {
        return new Request(TRACKING_ID, NAME, URI, DESTINATION, MEDIA_TYPE);
    }

    public static Request dummyRequest(Status status) {
        Request request = new Request(TRACKING_ID, NAME, URI, DESTINATION, MEDIA_TYPE);
        request.setStatus(status);
        return request;
    }
}

