package com.semi.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.semi.project.model.CombinedData;
import com.semi.project.model.MyPage;
import com.semi.project.model.UserRanking;
import com.semi.project.repository.MyPageRepository;
import com.semi.project.repository.PricesRepository;
import com.semi.project.repository.TradeTestRepository;
import com.semi.project.service.MyPageService;
import com.semi.project.service.TradeTestService;
import com.semi.project.service.UserService;
import com.semi.project.service.WebPageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class TradeTestRankingController {
	
	private final MyPageService myPageService;
	private final TradeTestRepository tradeTestRepository;
	private final PricesRepository pricesRepository;
	
	// 모의투자 랭킹 데이터 전달
    @GetMapping("/api/ranking/{coinCode}")
    public ResponseEntity<List<UserRanking>> getRankingByCurrency(@PathVariable String coinCode) {
    	List<Object[]> results;
    	if ("BTC".equals(coinCode)) {
    		results = tradeTestRepository.getRankingByBTC(coinCode);    		
    	} else {
    		results = tradeTestRepository.getRankingByETH(coinCode);
    	}
        
        List<UserRanking> rankingList = new ArrayList<>();

        Double currentCoinPrice = pricesRepository.findTopByCoincodeOrderByDateDesc(coinCode).getPrice();

        for (Object[] result : results) {
            String username = (String) result[0];
            Double profitLoss = (Double) result[2];
            
            CombinedData combinedData = myPageService.getCombinedData(username);

            Optional<MyPage> myPageData = combinedData.getMyPage();
            MyPage myPage = myPageData.get(); 

            Double sumBtcTradePrice = 0.0;
            Double sumEthTradePrice = 0.0;
            
            if ("BTC".equals(coinCode)) {
                sumBtcTradePrice = tradeTestRepository.buyCoinSum("BTC", username);
            } else {
                sumEthTradePrice = tradeTestRepository.buyCoinSum("ETH", username);
            }

            Double evaluationAmount = (coinCode.equals("BTC") ? sumBtcTradePrice : sumEthTradePrice) * currentCoinPrice;
            Double profitRate = (profitLoss / (coinCode.equals("BTC") ? sumBtcTradePrice : sumEthTradePrice)) * 100;

            UserRanking ranking = new UserRanking(username, evaluationAmount, profitLoss, profitRate);
            ranking.setSumBtcTradePrice(sumBtcTradePrice);
            ranking.setSumEthTradePrice(sumEthTradePrice);
            
            if(myPageData != null) {
                Double userBtc = myPage.getUserBtc();
                Double userEth = myPage.getUserEth();

                // 여기서 userBtc와 userEth를 사용하여 필요한 계산을 수행합니다.
                ranking.setUserBtc(userBtc);
                ranking.setUserEth(userEth);
            }

            rankingList.add(ranking);
        }

        return ResponseEntity.ok(rankingList);
    }
}
