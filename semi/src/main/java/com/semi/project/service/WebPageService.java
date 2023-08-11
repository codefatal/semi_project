package com.semi.project.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.semi.project.model.Coins;
import com.semi.project.model.Prices;
import com.semi.project.repository.CoinsRepository;
import com.semi.project.repository.PricesRepository;
import com.semi.project.service.external.Api_Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Web 페이지의 정보 노출을 위한 조회용 서비스
@RequiredArgsConstructor
@Service
@Slf4j
public class WebPageService {
	
	private final CoinsRepository coinsRepository;
	private final PricesRepository pricesRepository;
	
	// 빗썸에서 제공한 API_Client Class 정의 (API_Key와 Secret을 입력 / 조회 기능만 제공)
	static Api_Client apiClient = new Api_Client("951ec4df5c5f0fc4cb9b3cfca5999852", "6c2d1036700252139b5b3f3f130b04ee");
	
	// 내가 저장한 (관리할) 모든 코인의 목록을 가져온다
	// 현재 목록은 BTC, ETH만 있음
	public List<Coins> findAllCoins() throws Exception {
		return (List<Coins>) coinsRepository.findAll();
	}
	
	// 매개 변수로 받은 코인의 최근 10개 가격 데이터 저장
	public List<Prices> findPriceList(String coincode) throws Exception {
		return pricesRepository.findFirst10ByCoincodeOrderByDateDesc(coincode);
	}
}
