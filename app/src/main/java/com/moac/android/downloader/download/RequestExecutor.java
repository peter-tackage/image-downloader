package com.moac.android.downloader.download;

import android.os.Handler;

public interface RequestExecutor {
    public void submit(Job job);
    public boolean isIdle();
    public void shutdown();
    public void setOnPostExecuteHandler(Handler listener);

}
