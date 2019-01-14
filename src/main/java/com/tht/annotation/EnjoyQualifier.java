package com.tht.annotation;

import java.lang.annotation.*;

/**
 * Created by thrall on 2018/12/23.
 * 到容器中取value
 */
@Target(ElementType.FIELD)   //作用范围 成员变量
@Retention(RetentionPolicy.RUNTIME)  //系统运行时通过反射获取信息
@Documented   //javaDoc
public @interface EnjoyQualifier {
    //注解括号内可以跟值
    String value() default "";

}
