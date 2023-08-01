package com.solo.semi.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.solo.semi.model.News;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NewsService {
    private static String News_URL = "https://kr.tradingview.com/news/";

    @PostConstruct
    public List<News> getNewsDatas() throws IOException {
        System.setProperty("https.protocols", "TLSv1.2");
        List<News> newsList = new ArrayList<>();
        Document document = Jsoup.connect(News_URL).get();

        Elements contents = document.select("div.container-lu7Cy9jC div.grid-ScDiRUwB a");

        for(Element content : contents) {
            News news= News.builder()
                    .url(content.select("a").attr("abs:href"))
                    .time(content.select("article div div").text())
                    .subject(content.select("article > div > span").text())
                    .build();
            newsList.add(news);
        }

        return newsList;
    }

}
