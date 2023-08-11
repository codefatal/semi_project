package com.semi.project.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.semi.project.model.SiteUser;
import com.semi.project.model.TradeTest;
import com.semi.project.repository.TradeTestRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
@Transactional
public class TradeTestService {
	
	private final TradeTestRepository tradeTestRepository;
	private final UserService userService;

	// 매수/매도 진행 시 거래내역 정보 trade_test 테이블에 저장
	public TradeTest tradeTestLog(String coinCode, int tradeType, double orderamount, double tradeItem, double price) {
		SiteUser currentUser = userService.getCurrentUser();
		TradeTest trade = new TradeTest();
				
		String currentUsername = currentUser.getUsername();
		trade.setUsername(currentUsername);
		trade.setCoincode(coinCode);
		trade.setTradeType(tradeType);
		trade.setTradePrice(orderamount);
		trade.setTradeItem(tradeItem);
		trade.setTradeCoinMoney(price);
		trade.setDate(LocalDateTime.now());
		this.tradeTestRepository.save(trade);
		
		return trade;
	}
}
