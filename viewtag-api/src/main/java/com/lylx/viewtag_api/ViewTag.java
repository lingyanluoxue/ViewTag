package com.lylx.viewtag_api;

import android.app.Activity;
import android.util.Log;

/**
 * Created by zhanghongmei on 2016/10/11.
 */

public class ViewTag {
    public static void inject(Activity activity){
        inject(activity , activity);
    }
    public static void inject(Object host , Object root){
        Class<?> clazz = host.getClass();
        String proxyClassFullName = clazz.getName()+"$$ViewInjector";
        Class<?> proxyClazz = null;
        try {
            proxyClazz = Class.forName(proxyClassFullName);
            ViewInjector viewInjector = (com.lylx.viewtag_api.ViewInjector) proxyClazz.newInstance();
            viewInjector.inject(host,root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
