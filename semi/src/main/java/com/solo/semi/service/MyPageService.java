package com.solo.semi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solo.semi.model.CombinedData;
import com.solo.semi.model.MyPage;
import com.solo.semi.model.SiteUser;
import com.solo.semi.model.TradeTest;
import com.solo.semi.repository.MyPageRepository;
import com.solo.semi.repository.TradeTestRepository;

@Service
public class MyPageService {
	
	private final MyPageRepository myPageRepository;
    private final UserService userService;
    
    @Autowired
    private TradeTestRepository tradeTestRepository;

    public MyPageService(MyPageRepository myPageRepository, UserService userService) {
        this.myPageRepository = myPageRepository;
        this.userService = userService;
    }

    public Double getMoneyForCurrentUser() {
        SiteUser currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            String currentUsername = currentUser.getUsername();
            Optional<MyPage> myPageOptional = myPageRepository.findByUsername(currentUsername);

            if (myPageOptional.isPresent()) {
                MyPage myPage = myPageOptional.get();
                return myPage.getMoney();
            }
        }

        return 0.0; // 사용자 정보를 찾지 못하거나, MyPage 정보를 찾지 못한 경우 기본값으로 0 반환
    }
    
    public CombinedData getCombinedData(String username) {
    	Optional<TradeTest> tradeTestData = tradeTestRepository.findTopByUsernameOrderByDateDesc(username);
        Optional<MyPage> myPageData = myPageRepository.findByUsername(username);

        return new CombinedData(tradeTestData, myPageData); // 병합된 데이터를 반환하는 DTO
    }

}
