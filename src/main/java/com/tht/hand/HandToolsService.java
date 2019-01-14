package com.tht.hand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by thrall on 2019/1/14.
 */
public interface HandToolsService {

    public Object[]  hand(HttpServletRequest request, HttpServletResponse response,
                          Method method,
                          Map<String,Object> map);
}
