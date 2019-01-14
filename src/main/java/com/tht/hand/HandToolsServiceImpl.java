package com.tht.hand;

import com.tht.annotation.EnjoyService;
import com.tht.argumentResolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thrall on 2019/1/14.
 */
@EnjoyService("jamesHandTool")
public class HandToolsServiceImpl implements HandToolsService {

    //返回方法里的所有参数
    public Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method,
                         Map<String, Object> beans) {

        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];

        //argumentResolver 实例已经通过注解扫描到ioc容器了
        Map<String, Object> argResolvers = getBeansOfType(beans, ArgumentResolver.class);

        int paramIndex=0;
        int i=0;
        for(Class<?> paramClazz:paramClazzs){
            //哪个参数对应了哪个参数解析类,用策略模式来找
            for (Map.Entry<String, Object> entry : argResolvers.entrySet()) {
                ArgumentResolver ar = (ArgumentResolver)entry.getValue();
                if (ar.support(paramClazz, paramIndex, method)) {
                    args[i++] = ar.argumentResolver(request,
                            response,
                            paramClazz,
                            paramIndex,
                            method);
                }
            }
            paramIndex++;
        }

        return args;
    }

    //获取实现了ArgumentResolver接口的所有实例(其实就是每个参数的注解实例)
    /**
     * @param beans  所有bean
     * @param intfType  类型的实例
     * @return
     */
    private Map<String, Object> getBeansOfType(Map<String, Object> beans,
                                               Class<?> intfType) {
        Map<String, Object> resultBeans = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            //拿到实例-->反射对象-->它的接口(接口有多实现,所以为数组)
            Class<?>[] intfs = entry.getValue().getClass().getInterfaces();
            if (intfs != null && intfs.length > 0) {
                for (Class<?> intf : intfs) {
                    //接口的类型与传入进来的类型一样,把实例加到resultBeans里来
                    if (intf.isAssignableFrom(intfType)) {
                        resultBeans.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return resultBeans;
    }
}
