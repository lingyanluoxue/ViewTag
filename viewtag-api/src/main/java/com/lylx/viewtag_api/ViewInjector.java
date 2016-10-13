package com.lylx.viewtag_api;

/**
 * Created by zhanghongmei on 2016/10/11.
 */

public interface ViewInjector<T> {
    void inject(T t , Object object);
}
