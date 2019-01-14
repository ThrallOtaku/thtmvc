package com.tht.annotation;

import java.lang.annotation.*;

/**
 * Created by thrall on 2018/12/23.
 *
 */
@Target(ElementType.TYPE)   //作用范围  类
@Retention(RetentionPolicy.RUNTIME)  //系统运行时通过反射获取信息
@Documented   //javaDoc
//@Inherited   //这个注解表示可以被继承
public @interface EnjoyController {
    //注解括号内可以跟值
    String value() default "";

}
