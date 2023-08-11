package com.semi.project.controller;

import com.semi.project.model.News;
import com.semi.project.service.NewsService;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public String news(Model model) throws Exception{
        List<News> newsList = newsService.getNewsDatas();
        model.addAttribute("news", newsList);

        return "news";
    }
}
