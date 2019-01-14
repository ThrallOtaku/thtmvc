package com.tht.servlet;

import com.tht.annotation.*;
import com.tht.controller.JamesController;
import com.tht.hand.HandToolsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thrall on 2018/12/23.
 * springMvc的核心
 */
public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();
    Map<String, Object> beans = new HashMap<String, Object>();
    Map<String, Object> handlerMap = new HashMap<String, Object>();

    private static String Handler="jamesHandTool";

    public DispatcherServlet(){
        System.out.println("DispatcherServlet 构造");
    }

    //初始化<load-on-startup>0</load-on-startup>
    //tomcat 启动的时候要执行的内容，创建java map
    public void init(ServletConfig config) {
        System.out.println("DispatcherServlet init方法");
        //扫描com.tht.controller.JamesController Clazz<?>
        //1.扫描哪些类需要实例化
        doScan("com.tht");
        for(String name:classNames){
            System.out.println("cName:"+name);
        }
        //2.bean的实例化放到map    beans
        doInstance();
        System.out.println("实例化完成"+"beanSize:"+beans.entrySet().size());
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        //3.依赖注入
        iocDi();
        //4.路径和执行方法之间建立联系     /james  和当前方法关联起来
        urlMappingHandler();
        for(Map.Entry<String,Object> entry :handlerMap.entrySet()){
            System.out.println(entry.getKey()+""+entry.getValue());
        }
    }

    public void urlMappingHandler() {
        if(beans.entrySet().size()<=0){
            System.out.println("urlMappingHandler 类没有实例化");
        }

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(EnjoyController.class)){
                EnjoyRequestMapping reqMapping=clazz.getAnnotation(EnjoyRequestMapping.class);
                String classPath=reqMapping.value();
                Method[] methods= clazz.getMethods();
                for(Method method:methods){
                    //判断声明了那个注解
                    if(method.isAnnotationPresent(EnjoyRequestMapping.class)){
                        EnjoyRequestMapping reqMapping1=method.getAnnotation(EnjoyRequestMapping.class);
                        String methodUrl =reqMapping1.value();
                        //地址绑定方法放入map
                        handlerMap.put(classPath+methodUrl,method);
                    }else{
                        continue;
                    }
                }
            }else{
                continue;
            }
        }
    }

    public void iocDi() {
        if(beans.entrySet().size()<=0){
            System.out.println("没有bean被实例化");
            return;
        }

        //把service 注入到controller
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            //把service 注入到controller
            if (clazz.isAnnotationPresent(EnjoyController.class)) {
                //拿到成员变量从容器然后getInstance
                //也就是从controller 拿到成员变量注入到controller
                Field[] fileds = clazz.getDeclaredFields();
                //遍历成员变量
                for (Field fied : fileds) {
                    if (fied.isAnnotationPresent(EnjoyAutowired.class)) {
                        EnjoyAutowired auto = fied.getAnnotation(EnjoyAutowired.class);
                        String key = auto.value();
                        Object value = beans.get(key);
                        //放开权限，如果不加的话private
                        fied.setAccessible(true);
                        try {
                            fied.set(instance, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else if(fied.isAnnotationPresent(EnjoyQualifier.class)){
                        EnjoyQualifier qualifier=fied.getAnnotation(EnjoyQualifier.class);
                        String key=qualifier.value();
                        //放开权限，如果不加的话private 无法注入
                        fied.setAccessible(true);
                        //依赖注入
                        try {
                            fied.set(instance,beans.get(key));
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
        if(classNames.size()<=0){
            System.out.println("doScanFail------------------------------------------");
            return;
        }
        //遍历扫描到的全类名路径
        for (String className : classNames) {
            //com.enjoy.james.controller.JamesController.class 去掉.class
            String cn = className.replace(".class", "");
            try {
                //反射拿到对象
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(EnjoyController.class)) {
                    //对象实例化
                    Object value = clazz.newInstance();
                    EnjoyController controller = clazz.getAnnotation(EnjoyController.class);
                    EnjoyRequestMapping requestMapping=clazz.getAnnotation(EnjoyRequestMapping.class);
                    String key = requestMapping.value();
                    //IOC  map put
                    beans.put(key, value);
                } else if (clazz.isAnnotationPresent(EnjoyService.class)) {
                    Object value = clazz.newInstance();
                    EnjoyService service = clazz.getAnnotation(EnjoyService.class);
                    String key = service.value();
                    beans.put(key, value);   //就是放到ioc容器里面是个map对象
                    //TODO dao层实例化
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

    //扫描包方法
    public void doScan(String basePackage) {
        //basePackage=com.tht
        //扫描编译好的所有类路径
        URL url = this.getClass().getClassLoader().getResource("/" +
                basePackage.replaceAll("\\.", "/"));
        System.out.println("url:"+url);

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
        //获取到浏览器请求路径
        String uri=req.getRequestURI();   //thtmvc/tht/query.....

        String context=req.getContextPath();
        System.out.println("contextPath:"+context);

        String path=uri.replace(context,"");  //james/query  ---key

        Method method=(Method)handlerMap.get(path);
        //拿到控制类
        JamesController instance =(JamesController)beans.get("/"+path.split("/")[1]); //    /james

        //策略模式实现处理器
        HandToolsService handToolsService=(HandToolsService)beans.get(Handler);

        Object [] args=handToolsService.hand(req,resp,method,beans);
        try {
            //instance 实例，args参数
            method.invoke(instance,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
