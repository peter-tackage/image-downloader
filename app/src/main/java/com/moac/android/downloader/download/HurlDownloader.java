package com.moac.android.downloader.download;

import android.net.Uri;

import java.io.IOException;

/**
 * Implements a Downloader using HttpUrlConnection
 */
public class HurlDownloader implements Downloader{
    @Override
    public Response load(Uri uri, String destination) throws IOException{
        // TODO Implement the downloading logic
        Response response = new Response();
        response.mIsSuccess = true;
        return response;
    }
}
