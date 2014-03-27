package com.moac.android.downloader;

import android.content.Context;

import com.moac.android.downloader.injection.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, injects = DemoDownloaderApplication.class)
public class ApplicationModule {

    private final DemoDownloaderApplication application;

    public ApplicationModule(DemoDownloaderApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

}
