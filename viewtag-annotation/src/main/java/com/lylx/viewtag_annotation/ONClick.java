package com.lylx.viewtag_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by zhanghongmei on 2016/10/13.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnClick {
    int [] value() ;
}
