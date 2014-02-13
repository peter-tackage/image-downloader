package com.moac.android.downloader.request;

/*
 * A runnable implementation that performs a download
 */
public class DownloadJob implements Runnable {

    private final Downloader mDownloader;
    private final Request mRequest;

    public DownloadJob(Request request, Downloader downloader) {
        mRequest = request;
        mDownloader = downloader;
    }

    @Override
    public void run() {
        // TODO Set status
        mDownloader.load(mRequest.getUri(), mRequest.getDestination());
    }
}
