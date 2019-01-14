package com.tht.annotation;

/**
 * Created by thrall on 2018/12/23.
 */

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnjoyAutowired {
    //注解括号内可以跟值
    String value() default "";
}
