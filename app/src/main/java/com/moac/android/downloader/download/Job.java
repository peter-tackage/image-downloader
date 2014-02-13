package com.moac.android.downloader.download;

/*
 * A Runnable implementation that performs a download Request
 */
public class Job implements Runnable {

    private final Downloader mDownloader;
    private final Request mRequest;

    public Job(Request request, Downloader downloader) {
        mRequest = request;
        mDownloader = downloader;
    }

    @Override
    public void run() {
        // TODO Set status, perhaps use a Future to informs of result.
        mDownloader.load(mRequest.getUri(), mRequest.getDestination());
    }
}
