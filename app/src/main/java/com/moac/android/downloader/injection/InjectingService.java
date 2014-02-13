package com.moac.android.downloader.injection;

import android.app.Service;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public abstract class InjectingService extends Service implements Injector {

    private ObjectGraph mObjectGraph;

    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    @Override
    public void inject(Object target) {
        mObjectGraph.inject(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ((Injector) getApplication()).getObjectGraph().plus(getModules().toArray());
        inject(this);
    }

    @Override
    public void onDestroy() {
        mObjectGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return new ArrayList<Object>();
    }

}
