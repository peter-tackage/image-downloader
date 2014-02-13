package com.moac.android.downloader.download;

import android.net.Uri;

/**
 * A Downloader is responsible for implementing the mechanics of
 * retrieving bytes from a Uri location and storing them at a destination
 *
 * This allows for flexibility in HTTP Client implementations
 *
 * TODO This should return a result
 */
public interface Downloader {
    void load(Uri uri, String destination);
}
