package com.tht.servlet;

import com.tht.annotation.EnjoyAutowired;
import com.tht.annotation.EnjoyController;
import com.tht.annotation.EnjoyRequestMapping;
import com.tht.annotation.EnjoyService;
import com.tht.controller.JamesController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thrall on 2018/12/23.
 */
public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();
    Map<String, Object> beans = new HashMap<String, Object>();
    Map<String, Object> handerMap = new HashMap<String, Object>();

    //初始化<load-on-startup>0</load-on-startup>
    //tomcat 启动的时候要执行的内容，创建java  map———autoWired
    public void init(ServletConfig config) {
        //扫描com.tht.controller.JamesController Clazz<?>
        doScan("com.tht");

        doInstance();

        doAutowired();

        urlMapping();   //路径和执行方法之间建立联系
    }

    public void urlMapping() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(EnjoyController.class)){
                EnjoyRequestMapping reqMapping=clazz.getAnnotation(EnjoyRequestMapping.class);
                String classPath=reqMapping.value();
                Method[] methods= clazz.getMethods();
                for(Method method:methods){
                    if(method.isAnnotationPresent(EnjoyRequestMapping.class)){
                        EnjoyRequestMapping reqMapping1=method.getAnnotation(EnjoyRequestMapping.class);
                        String methodPath=reqMapping1.value();
                        handerMap.put(classPath+methodPath,method);
                    }else{
                        continue;
                    }
                }
            }else{
                continue;
            }
        }
    }

    public void doAutowired() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(EnjoyController.class)) {
                //拿到成员变量从容器然后getInstance
                Field[] fileds = clazz.getDeclaredFields();
                //遍历成员变量
                for (Field fied : fileds) {
                    if (fied.isAnnotationPresent(EnjoyAutowired.class)) {
                        EnjoyAutowired auto = fied.getAnnotation(EnjoyAutowired.class);
                        String key = auto.value();
                        Object value = beans.get(key);
                        fied.setAccessible(true);
                        try {
                            fied.set(instance, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                continue;
            }
        }
    }

    //遍历实例化
    public void doInstance() {
        for (String className : classNames) {
            //com.enjoy.james.controller.JamesController.class 去掉.class
            String cn = className.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(EnjoyController.class)) {
                    Object value = clazz.newInstance();
                    EnjoyRequestMapping reqMap = clazz.getAnnotation(EnjoyRequestMapping.class);
                    String key = reqMap.value();
                    beans.put(key, value);

                } else if (clazz.isAnnotationPresent(EnjoyService.class)) {
                    Object value = clazz.newInstance();
                    EnjoyService reqMap = clazz.getAnnotation(EnjoyService.class);
                    String key = reqMap.value();
                    beans.put(key, value);   //就是放到ioc容器里面是个map对象
                } else {
                    continue;
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //扫描方法
    public void doScan(String basePackage) {
        //basePackage=com.tht
        //扫描编译好的所有类路径
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        String[] filesStr = file.list();
        for (String path : filesStr) {
            File filePath = new File(fileStr + path);
            if (filePath.isDirectory()) {
                doScan(basePackage + "." + path);
            } else {
                //com.tht/......OrderService.class
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取到请求路径
        String uri=req.getRequestURI();   //thtmvc/tht/query.....
        String context=req.getContextPath();

        String path=uri.replace(context,"");  //james/query  ---key

        Method method=(Method)handerMap.get(path);

        JamesController instance =(JamesController)beans.get("/"+path.split("/")[1]); //    /james

        //TODO
       // method.invoke();

    }
}
