package com.tht.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by thrall on 2018/12/23.
 * spring的注解都是基于jdk提供的基础的annotation
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoSomething {
    //有默认值可以不赋值，没有默认值不可以不赋值
    String key();
    String cacheName();
    boolean needLog() default false;

}
