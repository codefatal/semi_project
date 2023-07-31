package com.solo.semi.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.semi.model.Coins;
import com.solo.semi.model.Prices;
import com.solo.semi.repository.CoinsRepository;
import com.solo.semi.repository.PricesRepository;
import com.solo.semi.service.external.Api_Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
// 일정 주기로 코인 가격정보를 저장하기 위한 서비스
public class SavePriceService {
	
	private final CoinsRepository coinsRepository;
	private final PricesRepository pricesRepository;
	
	// 빗썸에서 제공한 API_Client Class 정의 (API Key와 Secret를 입력)
	static Api_Client apiClient = new Api_Client("빗썸 API Key", "빗썸 API Secret Key");
	
	// 10분 동안 거래량 계산을 위한, 10분 전 가격 정보를 담을 Map
	static Map<String, Double> preVolumnMap = new HashMap<>();
	
	// 프로그램 실행 시 이전에 저장했던 가격 정보는 삭제
	@PostConstruct
	private void initDelAllPrices() throws Exception {
		log.info("[initDelAllPrices] 프로그램 최초 실행 시, 기존 Price 데이터 삭제(지운 데이터): " + pricesRepository.count() + ")");
		pricesRepository.deleteAll();
	}
	
	// 10분마다 코인의 가격과 거개량 정보를 저장
	@Scheduled(cron = "30 9,19,29,39,49,59 * * * *") // 매시 09:30, 19:30, 29:30, 39:30, 49:30, 59:30에 실행
	public void savePriceEvery10min() throws Exception {
		Prices currentPrice = new Prices();
		List<Coins> coins = (List<Coins>) coinsRepository.findAll();
		log.info("[savePriceEvery10min] 10분마다 가격 정보를 저장 (현재 시간: " + LocalDateTime.now() + ", 저장할 코인 수 : " + coins.size() + ")");
		
		double curPreGap = 0.0;
		
		for(Coins c : coins) {
			currentPrice = getCoinPrice(c.getCoincode());
			if(preVolumnMap.get(c.getCoincode()) == null ||
					pricesRepository.countByCoincode(c.getCoincode()) == 0) { // 최초 실행의 경우, 거래량은 0으로 현재가격을 저장
				preVolumnMap.put(c.getCoincode(), currentPrice.getVol());
				currentPrice.setVol(0.0);
			} else { // 직전 데이터가 있는 경우
				log.info(currentPrice.toString() + "|" + preVolumnMap.toString());
				curPreGap = currentPrice.getVol() - preVolumnMap.get(c.getCoincode());
				if(curPreGap >= 0) {
					currentPrice.setVol(curPreGap);
				} else { // 매일 00:09:30의 경우, 빗썸 API가 00시 기준으로 리셋이 되기 때문에 그 전날 23:59:30~24:00:00의 30초 동안 거래량은 버리고 00:00:00~00:09:30의 거래량만 저장
					currentPrice.setVol(currentPrice.getVol());
				}
			}
			pricesRepository.save(currentPrice); // DB에 저장
		}
	}
	
	// 빗썸 API를 통해 코인의 현재 가격 정보를 가져 옴
	private Prices getCoinPrice(String coinCode) throws Exception {
		Prices price = new Prices();
		// 빗썸 API 호출을 위한 URL 생성
		String url = "/public/ticker/";
		url = url + coinCode + "_KRW";
		// 빗썸 API 호출
		HashMap<String, String> params = new HashMap<>();
		String result = apiClient.callApi(url, params);
		// 응답받은 String 데이터를 Map 객체로 저장
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> m = (Map<String, String>) mapper.readValue(result, Map.class).get("data");
		// Price 객체에 각각 응답 값을 저장
		price.setCoincode(coinCode);
		price.setPrice(Double.parseDouble(m.get("closing_price")));
		price.setVol(Double.parseDouble(m.get("units_traded")));
		price.setDates(LocalDateTime.now());
		
		return price;
	}
}
