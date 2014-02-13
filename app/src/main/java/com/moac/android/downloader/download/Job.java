package com.moac.android.downloader.download;

/*
 * A runnable implementation that performs a download
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
        // TODO Set status
        mDownloader.load(mRequest.getUri(), mRequest.getDestination());
    }
}
