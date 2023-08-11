package com.semi.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.semi.project.model.CombinedData;
import com.semi.project.model.MyPage;
import com.semi.project.model.SiteUser;
import com.semi.project.model.TradeTest;
import com.semi.project.repository.MyPageRepository;
import com.semi.project.repository.TradeTestRepository;

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
