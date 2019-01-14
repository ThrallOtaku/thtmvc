package com.tht.service.impl;

import com.tht.annotation.EnjoyService;
import com.tht.service.JamesService;

/**
 * Created by thrall on 2018/12/23.
 */
@EnjoyService("JamesServiceImpl")
public class JamesServiceImpl  implements JamesService {
    public String query(String name, String age) {
        return "name==="+name+";age==="+age;
    }

    public String insert(String param) {
        return "insert successful.................";
    }

    public String update(String param) {
        return "update successful.................";
    }
}
