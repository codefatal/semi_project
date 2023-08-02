package com.solo.semi.service;

import com.solo.semi.model.MyPage;
import com.solo.semi.model.SiteUser;
import com.solo.semi.repository.MyPageRepository;
import com.solo.semi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MyPageRepository myPageRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        
        MyPage mypage = new MyPage();
        mypage.setMoney(1000000.0);
        mypage.setUserBtc(0.0000);
        mypage.setUserEth(0.0000);
        this.myPageRepository.save(mypage);
        
        return user;
    }
}