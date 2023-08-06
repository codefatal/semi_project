package com.solo.semi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solo.semi.model.TradeTest;
import com.solo.semi.repository.TradeTestRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class TradeTestService {
	
	private final TradeTestRepository tradeTestRepository;
	
	public TradeTest tradeTestLog(String coinCode, String tradeType, int tradePrice, double tradeItem, int tradeMoney) {
		TradeTest trade = new TradeTest();
		trade.setCoincode(null);
		trade.setTradeType(null);
		trade.setTradePrice(null);
		trade.setTradeItem(null);
		trade.setTradeMoney(null);
		this.tradeTestRepository.save(trade);
	}
}
