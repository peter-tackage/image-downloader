package com.moac.android.downloader.download;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

/**
 * A {@link com.moac.android.downloader.download.Downloader} that is implemented
 * using HttpUrlConnection
 */
public class HurlDownloader implements Downloader {

    private static final String TAG = HurlDownloader.class.getSimpleName();
    static final int DEFAULT_READ_TIMEOUT = 20 * 1000; // 20sec
    static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000; // 15sec

    @Override
    public NetworkResponse load(Uri uri, String destination) throws IOException {
        Log.i(TAG, String.format("Downloading %s to %s ", uri.toString(), destination));

        HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setUseCaches(false);

        // Inspect HTTP response code (2xx range ok)
        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode > 299) {
            connection.disconnect();
            throw new ResponseException(responseCode, connection.getResponseMessage());
        }
        long contentLength = connection.getHeaderFieldInt("Content-Length", 0);
        return new NetworkResponse(connection.getInputStream(), contentLength);
    }
}
