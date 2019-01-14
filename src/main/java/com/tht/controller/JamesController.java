package com.tht.controller;

import com.tht.annotation.*;
import com.tht.service.JamesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by thrall on 2018/12/23.
 */
@EnjoyController
@EnjoyRequestMapping("/tht")
public class JamesController {

    //tomcat启动的时候会实例化jamesServiceImpl，并put到map
    //这里的EnjoyAutowired就是从map里get
    //@Autowired 也可以注入
    @EnjoyQualifier("JamesServiceImpl")
    private JamesService jamesService;

    @EnjoyRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @EnjoyRequestParam("name") String name,
                      @EnjoyRequestParam("age") String age) {
        //使用原生的返回方式
        PrintWriter ps = null;
        try {
            ps = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = jamesService.query(name, age);
        ps.write(result);
    }
}
