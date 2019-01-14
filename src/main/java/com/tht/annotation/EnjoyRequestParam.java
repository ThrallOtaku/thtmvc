package com.tht.annotation;

import java.lang.annotation.*;

/**
 * Created by thrall on 2018/12/23.
 */
@Target(ElementType.PARAMETER) //作用范围方法内的参数
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnjoyRequestParam {
    //注解括号内可以跟值
    String value() default "";
}
