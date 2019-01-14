package com.tht.controller;

import com.tht.annotation.EnjoyAutowired;
import com.tht.annotation.EnjoyController;
import com.tht.annotation.EnjoyRequestMapping;
import com.tht.annotation.EnjoyRequestParam;
import com.tht.service.JamesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by thrall on 2018/12/23.
 */
@EnjoyController
@EnjoyRequestMapping("/james")
public class JamesController {

    //tomcat启动的时候会实例化jamesServiceImpl，并put到map
    //这里的EnjoyAutowired就是从map里get
    @EnjoyAutowired
    private JamesService jamesService;

    @EnjoyRequestMapping("query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @EnjoyRequestParam("name") String name,
                      @EnjoyRequestParam("age") String age) {
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
