package com.solo.semi.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solo.semi.model.SiteUser;
import com.solo.semi.model.TradeTest;
import com.solo.semi.repository.TradeTestRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class TradeTestService {
	
	private final TradeTestRepository tradeTestRepository;
	private final UserService userService;
	
	public TradeTest tradeTestLog(String coinCode, int tradeType, double tradePrice, double tradeItem, double price) {
		SiteUser currentUser = userService.getCurrentUser();
		TradeTest trade = new TradeTest();
				
		String currentUsername = currentUser.getUsername();
		trade.setUsername(currentUsername);
		trade.setCoincode(coinCode);
		trade.setTradeType(tradeType);
		trade.setTradePrice(tradePrice);
		trade.setTradeItem(tradeItem);
		trade.setTradeCoinMoney(price);
		trade.setDate(LocalDateTime.now());
		this.tradeTestRepository.save(trade);
		
		return trade;
	}
}
