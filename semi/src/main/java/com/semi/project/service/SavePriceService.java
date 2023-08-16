package com.semi.project.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semi.project.model.Coins;
import com.semi.project.model.Prices;
import com.semi.project.repository.CoinsRepository;
import com.semi.project.repository.PricesRepository;
import com.semi.project.service.external.Api_Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 일정 주기로 코인 가격정보를 저장하기 위한 서비스
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class SavePriceService {
	
	private final CoinsRepository coinsRepository;
	private final PricesRepository pricesRepository;
	
	// 빗썸에서 제공한 API_Client Class 정의 (API Key와 Secret를 입력)
	static Api_Client apiClient = new Api_Client("951ec4df5c5f0fc4cb9b3cfca5999852", "6c2d1036700252139b5b3f3f130b04ee");
	
	// 10분 동안 거래량 계산을 위한, 10분 전 가격 정보를 담을 Map
	static Map<String, Double> preVolumeMap = new HashMap<>();
	
	// 프로그램 실행 시 이전에 저장했던 가격 정보는 삭제
	@PostConstruct
	private void initDelAllPrices() throws Exception {
		log.info("[initDelAllPrices] 프로그램 최초 실행 시, 기존 Price 데이터 삭제(지운 데이터): " + pricesRepository.count() + ")");
		pricesRepository.deleteAll();
		
		// 바로 데이터를 한번 불러오는 코드 추가
		savePriceEvery1min();
	}
	
	// 1분마다 코인의 가격과 거래량 정보를 저장
	@Scheduled(cron = "0 0/1 * * * *") // 매분 00초 마다 실행
	public void savePriceEvery1min() throws Exception {
		Prices currentPrice = new Prices();
		List<Coins> coins = (List<Coins>) coinsRepository.findAll();
		log.info("[savePriceEvery1min] 1분마다 가격 정보를 저장 (현재 시간: " + LocalDateTime.now() + ", 저장할 코인 수 : " + coins.size() + ")");
		
		double curPreGap = 0.0;
		
		for(Coins c : coins) {
			currentPrice = getCoinPrice(c.getCoincode());
			if(preVolumeMap.get(c.getCoincode()) == null ||
					pricesRepository.countByCoincode(c.getCoincode()) == 0) { // 최초 실행의 경우, 거래량은 0으로 현재가격을 저장
				preVolumeMap.put(c.getCoincode(), currentPrice.getVolume());
				currentPrice.setVolume(0.0);
			} else { // 직전 데이터가 있는 경우
				log.info(currentPrice.toString() + "|" + preVolumeMap.toString());
				curPreGap = currentPrice.getVolume() - preVolumeMap.get(c.getCoincode());
				if(curPreGap >= 0) {
					currentPrice.setVolume(curPreGap);
				} else { // 매일 00:09:30의 경우, 빗썸 API가 00시 기준으로 리셋이 되기 때문에 그 전날 23:59:30~24:00:00의 30초 동안 거래량은 버리고 00:00:00~00:09:30의 거래량만 저장
					currentPrice.setVolume(currentPrice.getVolume());
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
		price.setVolume(Double.parseDouble(m.get("units_traded")));
		price.setDate(LocalDateTime.now());
		
		return price;
	}
}
