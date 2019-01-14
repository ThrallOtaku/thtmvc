package com.tht.service.impl;

import com.tht.annotation.EnjoyService;
import com.tht.service.JamesService;

/**
 * Created by thrall on 2018/12/23.
 */
@EnjoyService
public class JamesServiceImpl  implements JamesService {
    public String query(String name, String age) {
        return "name==="+name+";age==="+age;
    }
}
