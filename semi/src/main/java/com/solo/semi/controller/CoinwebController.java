package com.solo.semi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.solo.semi.model.Coins;
import com.solo.semi.model.MyPage;
import com.solo.semi.model.Prices;
import com.solo.semi.model.SiteUser;
import com.solo.semi.model.TradeTest;
import com.solo.semi.repository.MyPageRepository;
import com.solo.semi.repository.TradeTestRepository;
import com.solo.semi.service.TradeTestService;
import com.solo.semi.service.UserService;
import com.solo.semi.service.WebPageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CoinwebController {
	
	private final WebPageService webPageService;
	private final MyPageRepository myPageRepository;
	private final TradeTestRepository tradeTestRepository;
	private final UserService userService;
	private final TradeTestService tradeTestService;
	
	@GetMapping("/coin")
    public String coinPage(Model model) throws Exception {
        List<Coins> coinList = webPageService.findAllCoins();
        model.addAttribute("coinList", coinList);

        List<Prices> priceList = new ArrayList<>();
        priceList = webPageService.findPriceList(coinList.get(0).getCoincode());
        model.addAttribute("priceList", priceList);

        SiteUser currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            String currentUsername = currentUser.getUsername();
            Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUsername);
            MyPage myPage = myPageOptional.orElse(new MyPage());
            double maxQuantity = myPage.getMoney() / priceList.get(0).getPrice();
            double maxQuantityRoundedDown = Math.floor(maxQuantity * 100000) / 100000.0;
            model.addAttribute("maxQuantityRoundedDown", maxQuantityRoundedDown);
            model.addAttribute("myPage", myPage);
        } else {
            model.addAttribute("myPage", new MyPage()); // 미인증 사용자인 경우 빈 MyPage 객체를 추가
        }

        return "crytotest";
    }
	
	@GetMapping("/coin/coinlist")
    @ResponseBody
    public ResponseEntity<List<Coins>> getCoinList() throws Exception {
		List<Coins> coinList = webPageService.findAllCoins();
        
        if (coinList.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        return ResponseEntity.ok(coinList); // 200 OK
    }
	
	@GetMapping("/coin/prices") // AJAX 구현을 위한 Price 데이터 전달 메소드
	public String getCoinPrices(Model model, @RequestParam String coinCode) throws Exception {
		List<Prices> priceList = new ArrayList<>();
		priceList = webPageService.findPriceList(coinCode); // 코인코드를 파라미터로 받아, DB 조회 후 가격 정보를 전달
		model.addAttribute("priceList", priceList);
		return "crytotest :: priceTable"; // thymeleaf AJAX 구현을 위해, 데이터가 변경될 떄 " :: ID" 추가
	}

    @GetMapping("/coin/prices/priceslist") // AJAX 구현을 위한 Price 데이터 전달 메소드
    @ResponseBody // JSON 데이터를 반환하므로 @ResponseBody 어노테이션 추가
    public List<Prices> getCoinPricesList(@RequestParam String coinCode) throws Exception {
        List<Prices> priceList = webPageService.findPriceList(coinCode); // 코인코드를 파라미터로 받아, DB 조회 후 가격 정보를 전달
        return priceList;
    }
    
    @GetMapping("/coin/prices/mypage")
    @ResponseBody
    public ResponseEntity<MyPage> getCoinMyPage() throws Exception {
        SiteUser currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 Unauthorized
        }

        String currentUsername = currentUser.getUsername();
        Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUsername);
        
        if (!myPageOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        return ResponseEntity.ok(myPageOptional.get()); // 200 OK
    }
    
    @PostMapping("/coin/prices/buy")
    public ResponseEntity<?> buyCoinMyPage(@RequestBody MyPage request) {
    	SiteUser currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            // 사용자가 없을 때의 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        String currentUsername = currentUser.getUsername();
        Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUsername);
        
        if (myPageOptional.isPresent()) {
            MyPage myPage = myPageOptional.get();
            // myPage에 request에서 받은 money 및 userBtc를 사용하여 업데이트
            myPage.setMoney(request.getMoney());
            
            // 받은 코인 코드를 기반으로 userBtc 또는 userEth를 업데이트
            String coinCode = request.getCoincode();
            double usercoin = 0.0;
            if ("BTC".equals(coinCode)) {
                myPage.setUserBtc(request.getUserBtc());
                usercoin = request.getUserBtc();
            } else if ("ETH".equals(coinCode)) {
                myPage.setUserEth(request.getUserEth());
                usercoin = request.getUserEth();
            }
            
            // myPage 저장
            myPageRepository.save(myPage);
            
            int tradetype = 1;
            
            tradeTestService.tradeTestLog(coinCode, tradetype, request.getOrderAmount(), usercoin, request.getPrice());
            // JSON 응답을 사용하여 리다이렉트를 권장
            Map<String, String> response = new HashMap<>();
            response.put("message", "Updated successfully.");
            response.put("redirectUrl", "/coin");
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    
    @PostMapping("/coin/prices/sell")
    public ResponseEntity<?> sellCoinMyPage(@RequestBody MyPage request) {
    	SiteUser currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            // 사용자가 없을 때의 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        String currentUsername = currentUser.getUsername();
        Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUsername);

        if (myPageOptional.isPresent()) {
            MyPage myPage = myPageOptional.get();
            
            // myPage에 request에서 받은 money 및 userBtc를 사용하여 업데이트
            myPage.setMoney(request.getMoney());
            
            // 받은 코인 코드를 기반으로 userBtc 또는 userEth를 업데이트
            String coinCode = request.getCoincode();
            double usercoin = 0.0;
            if ("BTC".equals(coinCode)) {
                myPage.setUserBtc(request.getUserBtc());
                usercoin = request.getUserBtc();
            } else if ("ETH".equals(coinCode)) {
                myPage.setUserEth(request.getUserEth());
                usercoin = request.getUserEth();
            }
            
            int tradetype = 2;
            
            tradeTestService.tradeTestLog(coinCode, tradetype, request.getOrderAmount(), usercoin, request.getPrice());

            // myPage 저장
            myPageRepository.save(myPage);

            // JSON 응답을 사용하여 리다이렉트를 권장
            Map<String, String> response = new HashMap<>();
            response.put("message", "Updated successfully.");
            response.put("redirectUrl", "/coin");
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    
    @GetMapping("/maxquantity")
    public ResponseEntity<?> getMaxQuantity(@RequestParam String coinCode) throws Exception {
        List<Prices> priceList = webPageService.findPriceList(coinCode);
        if (priceList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Price not found for coin code: " + coinCode);
        }

        SiteUser currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUser.getUsername());
            MyPage myPage = myPageOptional.orElse(new MyPage());

            double maxQuantity = myPage.getMoney() / priceList.get(0).getPrice();
            double maxQuantityRoundedDown = Math.floor(maxQuantity * 100000) / 100000.0;

            return ResponseEntity.ok(maxQuantityRoundedDown);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }
    }

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
