package com.lance.common.widget.template;

import android.app.Application;
import android.content.Context;

/**
 * Created by lindan on 17-3-30.
 * 用于获取全局Application Context
 */
public class ContextHolder {
    private static Context applicationContext;

    /**
     * 初始化context，如果由于不同机型导致反射获取context失败可以在Application调用此方法
     *
     * @param context Context
     */
    public static void initial(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static Context getContext() {
        if (applicationContext == null) {
            try {
                Application application = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    applicationContext = application;
                    return application;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Application application = (Application) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    applicationContext = application;
                    return application;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalStateException("ContextHolder is not initialed, it is recommend to init with application context.");
        }
        return applicationContext;
    }
}