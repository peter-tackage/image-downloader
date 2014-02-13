package com.moac.android.downloader;

import com.moac.android.downloader.injection.AppModule;
import com.moac.android.downloader.injection.InjectingApplication;
import com.moac.android.downloader.injection.Injector;

import java.util.List;

public class DownloaderTestApplication extends InjectingApplication implements Injector {

    @Override
    public List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new AppModule(this));
        return modules;
    }
}
