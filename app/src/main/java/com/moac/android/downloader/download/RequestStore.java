package com.moac.android.downloader.download;

import android.net.Uri;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestStore {

    private static final String TAG = RequestStore.class.getSimpleName();

    private Map<String, Request> mRequestMap = Collections.synchronizedMap(new HashMap<String, Request>());

    public Status getStatus(String id) {
        Request request = mRequestMap.get(id);
        return request == null ? Status.UNKNOWN : request.getStatus();
    }

    public Request getRequest(String id) {
        return mRequestMap.get(id);
    }

    public Request create(String id, Uri uri, String destination) {
        Request request = new Request(id, uri, destination);
        request.setStatus(Status.CREATED);
        mRequestMap.put(id, request);
        Log.i(TAG, "Created Request has status:" + request.getStatus());
        return request;
    }

}
