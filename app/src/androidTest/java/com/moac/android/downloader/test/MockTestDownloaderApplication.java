package com.moac.android.downloader.test;

import android.test.mock.MockApplication;

import com.moac.android.downloader.injection.Injector;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public class MockTestDownloaderApplication extends MockApplication implements Injector {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        // Note: No super.onCreate() as it is not implemented for MockApplication
        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.inject(this);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    @Override
    public void inject(Object target) {
        mObjectGraph.inject(target);
    }

    public List<Object> getModules() {
        ArrayList<Object> modules = new ArrayList<Object>();
        modules.add(new TestModule());
        return modules;
    }
}
