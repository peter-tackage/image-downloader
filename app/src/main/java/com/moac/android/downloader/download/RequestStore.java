package com.moac.android.downloader.download;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * Hold all requests, allows for querying of outcome.
 * Currently does not store persistently
 */
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

    public void add(Request request) {
        mRequestMap.put(request.getId(), request);
    }
}
