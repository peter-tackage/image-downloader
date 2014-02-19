package com.moac.android.downloader.injection;

import dagger.ObjectGraph;

public interface Injector {
    public ObjectGraph getObjectGraph();

    public void inject(Object target);
}