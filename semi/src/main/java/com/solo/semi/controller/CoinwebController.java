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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.solo.semi.model.Coins;
import com.solo.semi.model.MyPage;
import com.solo.semi.model.Prices;
import com.solo.semi.model.SiteUser;
import com.solo.semi.repository.MyPageRepository;
import com.solo.semi.service.UserService;
import com.solo.semi.service.WebPageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CoinwebController {
	
	private final WebPageService webPageService;
	private final MyPageRepository myPageRepository;
	private final UserService userService;
	
	@GetMapping("/coin")
    public String coinPage(Model model) throws Exception {
        List<Coins> coinList = webPageService.findAllCoins();
        model.addAttribute("coinList", coinList);

        List<Prices> priceList = new ArrayList<>();
        priceList = webPageService.findPriceList(coinList.get(0).getCoincode());
        model.addAttribute("priceList", priceList);

        SiteUser currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            Long currentUserId = currentUser.getId();
            Optional<MyPage> myPageOptional = myPageRepository.findById(currentUserId);
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

        Long currentUserId = currentUser.getId();
        Optional<MyPage> myPageOptional = myPageRepository.findById(currentUserId);
        
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

        Long currentUserId = currentUser.getId();
        Optional<MyPage> myPageOptional = myPageRepository.findById(currentUserId);
        
        if (myPageOptional.isPresent()) {
            MyPage myPage = myPageOptional.get();
            
            // myPage에 request에서 받은 money 및 userBtc를 사용하여 업데이트
            myPage.setMoney(request.getMoney());
            myPage.setUserBtc(request.getUserBtc());

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
    
    @PostMapping("/coin/prices/sell")
    public ResponseEntity<?> sellCoinMyPage(@RequestBody MyPage request) {
    	SiteUser currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            // 사용자가 없을 때의 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        Long currentUserId = currentUser.getId();
        Optional<MyPage> myPageOptional = myPageRepository.findById(currentUserId);

        if (myPageOptional.isPresent()) {
            MyPage myPage = myPageOptional.get();
            
            // myPage에 request에서 받은 money 및 userBtc를 사용하여 업데이트
            myPage.setMoney(request.getMoney());
            myPage.setUserBtc(request.getUserBtc());

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
    
}
