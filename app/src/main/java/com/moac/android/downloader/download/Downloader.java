package com.moac.android.downloader.download;

import android.net.Uri;

import java.io.IOException;

/**
 * A Downloader is responsible for implementing the mechanics of
 * retrieving bytes from a Uri location.
 *
 * This allows for flexibility in HTTP Client implementations, including
 * the ability to add any required request headers for downloads requiring
 * authorization.
 *
 */
public interface Downloader {
    NetworkResponse load(Uri uri, String destination) throws IOException;
}
