package com.lance.common.widget.template;

import android.app.Application;

/**
 * Created by lindan on 17-3-30.
 * Application模板基类
 */
public abstract class BaseApplication extends Application {
    public abstract void initConfigs();
    protected boolean isInit;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.initial(this);
        initConfigs();
        isInit = true;
    }
}
