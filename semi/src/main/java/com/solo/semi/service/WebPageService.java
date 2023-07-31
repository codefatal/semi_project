package com.solo.semi.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.solo.semi.model.Coins;
import com.solo.semi.model.Prices;
import com.solo.semi.repository.CoinsRepository;
import com.solo.semi.repository.PricesRepository;
import com.solo.semi.service.external.Api_Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Web 페이지의 정보 노출을 위한 조회용 서비스
@RequiredArgsConstructor
@Service
@Slf4j
public class WebPageService {
	
	private final CoinsRepository coinsRepository;
	private final PricesRepository pricesRepository;
	
	// 빗썸에서 제공한 API_Client Class 정의 (API_Key와 Secret을 입력)
	static Api_Client apiClient = new Api_Client("빗썸 API Key", "빗썸 API Secret Key");
	
	// 내가 저장한 (관리할) 모든 코인의 목록을 가져온다
	public List<Coins> findAllCoins() throws Exception {
		return (List<Coins>) coinsRepository.findAll();
	}
	
	// 매개 변수로 받은 코인의 최근 10개 가격 데이터 저장
	public List<Prices> findPriceList(String coincode) throws Exception {
		return pricesRepository.findFirst10ByCoincodeOrderByDateDesc(coincode);
	}
}
