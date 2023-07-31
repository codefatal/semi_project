package com.solo.semi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.solo.semi.model.Coins;
import com.solo.semi.model.Prices;
import com.solo.semi.service.WebPageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CoinwebController {
	private final WebPageService webPageService;
	
	@GetMapping("/coin")
	public String coinPage(Model model) throws Exception {
		List<Coins> coinList = webPageService.findAllCoins();
		model.addAttribute("coinList", coinList);
		
		List<Prices> priceList = new ArrayList<>();
		priceList = webPageService.findPriceList(coinList.get(0).getCoincode());
		model.addAttribute("priceList", priceList);
		
		return "crytotest";
	}
	
	@GetMapping("/coin/prices") // AJAX 구현을 위한 Price 데이터 전달 메소드
	public String getCoinPrices(Model model, @RequestParam String coinCode) throws Exception {
		List<Prices> priceList = new ArrayList<>();
		priceList = webPageService.findPriceList(coinCode); // 코인코드를 파라미터로 받아, DB 조회 후 가격 정보를 전달
		model.addAttribute("priceList", priceList);
		return "crytotest :: priceTable"; // thymeleaf AJAX 구현을 위해, 데이터가 변경될 떄 " :: ID" 추가
	}
}
