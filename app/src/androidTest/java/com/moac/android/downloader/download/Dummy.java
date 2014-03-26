package com.moac.android.downloader.download;

public class Dummy {

    public static Request dummyRequest() {
        return new Request(null, null, null, null, null);
    }

    public static Request dummyRequest(Status status) {
        Request request = new Request(null, null, null, null, null);
        request.setStatus(status);
        return request;
    }
}

