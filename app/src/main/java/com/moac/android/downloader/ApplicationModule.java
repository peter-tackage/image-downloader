package com.moac.android.downloader;

import android.content.Context;

import com.moac.android.downloader.injection.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, injects = TestDownloaderApplication.class)
public class ApplicationModule {

    private final TestDownloaderApplication application;

    public ApplicationModule(TestDownloaderApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

}
