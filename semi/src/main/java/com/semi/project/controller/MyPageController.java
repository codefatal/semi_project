package com.semi.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.semi.project.model.Coins;
import com.semi.project.model.MyPage;
import com.semi.project.model.Prices;
import com.semi.project.model.SiteUser;
import com.semi.project.model.TradeTest;
import com.semi.project.repository.MyPageRepository;
import com.semi.project.repository.TradeTestRepository;
import com.semi.project.service.UserService;
import com.semi.project.service.WebPageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MyPageController {
	
	private final WebPageService webPageService;
	private final UserService userService;
	private final MyPageRepository myPageRepository;
	private final TradeTestRepository tradeTestRepository;
	
	// 마이페이지 접속
    @GetMapping("/mypage")
    public String trade(Model model) throws Exception {
    	List<Coins> coinList = webPageService.findAllCoins();
        model.addAttribute("coinList", coinList);
        
        SiteUser currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            String currentUsername = currentUser.getUsername();
            Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUsername);
            MyPage myPage = myPageOptional.orElse(new MyPage());
            // 전체 계산
            Double sumTradePrice = tradeTestRepository.buySum(currentUsername);
            model.addAttribute("sumTradePrice", sumTradePrice);
            
            for (Coins coin : coinList) {
                List<Prices> priceList = webPageService.findPriceList(coin.getCoincode());
                model.addAttribute(coin.getCoincode() + "priceList", priceList);
            }
            
            // BTC 계산
            String BTC = "BTC";
            Double sumBtcTradePrice = tradeTestRepository.buyCoinSum(BTC, currentUsername);
            model.addAttribute("sumBtcTradePrice", sumBtcTradePrice);
            Double avgBtcTradePrice = tradeTestRepository.buyCoinAvg(BTC, currentUsername);
            model.addAttribute("avgBtcTradePrice", avgBtcTradePrice);
            
            // ETH 계산
            String ETH = "ETH";
            Double sumEthTradePrice = tradeTestRepository.buyCoinSum(ETH, currentUsername);
            model.addAttribute("sumEthTradePrice", sumEthTradePrice);
            Double avgEthTradePrice = tradeTestRepository.buyCoinAvg(ETH, currentUsername);
            model.addAttribute("avgEthTradePrice", avgEthTradePrice);

            model.addAttribute("myPage", myPage);
        } else {
            model.addAttribute("myPage", new MyPage()); // 미인증 사용자인 경우 빈 MyPage 객체를 추가
        }

    	return "mypage";
    }
    
    // 마이페이지 -> 거래내역에서 버튼을 클릭했을때
    @GetMapping("/mypage/tradelist/{type}")
    public String tradeBuyList(@PathVariable String type, Model model) throws Exception {
    	SiteUser currentUser = userService.getCurrentUser();
    	
    	if (currentUser != null) {
    		String currentUsername = currentUser.getUsername();
    		
    		List<TradeTest> tradeList = new ArrayList<>();
    		
    		switch(type) {
	            case "all":
	            	tradeList = tradeTestRepository.tradeList(currentUsername);
	                break;
	            case "buy":
	            	tradeList = tradeTestRepository.tradeBuyList(currentUsername);
	                break;
	            case "sell":
	            	tradeList = tradeTestRepository.tradeSellList(currentUsername);
	                break;
	            default:
	                // Handle unknown type
	                break;
    		}
    		model.addAttribute("tradeList", tradeList);
    		model.addAttribute("currentUsername", currentUsername);
    	} else {
    		model.addAttribute("tradeList", new ArrayList<TradeTest>());
    	}
    	
    	return "tradelist";
    }
}
