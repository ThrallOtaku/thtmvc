package com.tht.annotation;

import java.lang.annotation.*;

/**
 * Created by thrall on 2018/12/23.
 */
@Target({ElementType.TYPE,ElementType.METHOD}) //作用范围类和方法
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnjoyRequestMapping {
    //注解括号内可以跟值
    String value() default "";
}
