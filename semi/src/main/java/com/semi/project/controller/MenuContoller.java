package com.semi.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuContoller {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/crytochart")
    public String crytoChart() {
        return "crytochart";
    }

    @GetMapping("/crytomarket")
    public String crytoMarket() {
        return "crytomarket";
    }

}
