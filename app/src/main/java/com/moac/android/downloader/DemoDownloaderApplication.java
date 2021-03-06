package com.moac.android.downloader;

import com.moac.android.downloader.injection.InjectingApplication;
import com.moac.android.downloader.injection.Injector;

import java.util.List;

public class DemoDownloaderApplication extends InjectingApplication implements Injector {

    @Override
    public List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new ApplicationModule(this));
        return modules;
    }
}
