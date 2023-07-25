package com.solo.semi;

import org.springframework.web.bind.annotation.GetMapping;

public class Index {

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
