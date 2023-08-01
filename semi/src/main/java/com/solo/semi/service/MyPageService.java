package com.solo.semi.service;

import org.springframework.stereotype.Service;

import com.solo.semi.model.MyPage;
import com.solo.semi.model.SiteUser;
import com.solo.semi.repository.MyPageRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MyPageService {
	
	private final MyPageRepository myPageRepository;
	
	public MyPage createMyPageForUser(SiteUser user) {
		MyPage myPage = new MyPage();
		myPage.setMoney(100000000.0);
		myPage.setUserBtc(0.0);
		myPage.setUserEth(0.0);
		
		// SiteUser와 MyPage 간의 연결 설정
        myPage.setUser(user);
        user.setMypage(myPage);
		
		this.myPageRepository.save(myPage);
		return myPage;
	}
}
