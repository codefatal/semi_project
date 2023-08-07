package com.solo.semi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrytoContoller {

    @GetMapping("/index")
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

    @GetMapping("/trade")
    public String trade() {
    	return "trade";
    }

}
