package com.solo.semi.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.solo.semi.model.MyPage;
import com.solo.semi.model.SiteUser;
import com.solo.semi.repository.MyPageRepository;


@Service
public class MyPageService {
	
	private final MyPageRepository myPageRepository;
    private final UserService userService;

    public MyPageService(MyPageRepository myPageRepository, UserService userService) {
        this.myPageRepository = myPageRepository;
        this.userService = userService;
    }

    public Double getMoneyForCurrentUser() {
        SiteUser currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            Long currentUserId = currentUser.getId();
            Optional<MyPage> myPageOptional = myPageRepository.findById(currentUserId);

            if (myPageOptional.isPresent()) {
                MyPage myPage = myPageOptional.get();
                return myPage.getMoney();
            }
        }

        return 0.0; // 사용자 정보를 찾지 못하거나, MyPage 정보를 찾지 못한 경우 기본값으로 0 반환
    }
}
