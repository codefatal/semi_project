package com.semi.project.controller;

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

import com.semi.project.model.Coins;
import com.semi.project.model.CombinedData;
import com.semi.project.model.MyPage;
import com.semi.project.model.Prices;
import com.semi.project.model.SiteUser;
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
public class TradeTestController {
	
	private final WebPageService webPageService;
	private final UserService userService;
	private final TradeTestService tradeTestService;
	private final MyPageRepository myPageRepository;
	
	// 모의투자 페이지
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
	
	// 코인 리스트를 불러옴
	@GetMapping("/coin/coinlist")
    @ResponseBody
    public ResponseEntity<List<Coins>> getCoinList() throws Exception {
		List<Coins> coinList = webPageService.findAllCoins();
        
        if (coinList.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        return ResponseEntity.ok(coinList); // 200 OK
    }
	
	// 1분당 코인 가격 변동 사항 리스트 날짜 내림차순으로 출력
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
    
    // 마이페이지의 정보를 불러오는데 사용
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
    
    // 모의투자 페이지에서 매수 버튼을 눌렀을 경우
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
    
    // 모의투자 페이지에서 매도 버튼을 눌렀을 경우
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
    
    // 현재 최대 주문 가능 수량 계산
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
    
}
