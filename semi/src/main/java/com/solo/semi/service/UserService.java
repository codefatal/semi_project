package com.solo.semi.service;

import com.solo.semi.model.MyPage;
import com.solo.semi.model.SiteUser;
import com.solo.semi.repository.MyPageRepository;
import com.solo.semi.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public SiteUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 미인증 사용자 또는 로그인하지 않은 경우 null 반환
        }

        String username = authentication.getName();
        Optional<SiteUser> userOptional = userRepository.findByUsername(username);

        return userOptional.orElse(null);
    }
    
    @Transactional
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        
        MyPage mypage = new MyPage();
        mypage.setUsername(username);
        mypage.setMoney(1000000.0); // 모의투자용 기본금액
        mypage.setUserBtc(0.0000); // 기본 BTC
        mypage.setUserEth(0.0000); // 기본 ETH
        this.myPageRepository.save(mypage);
        
        return user;
    }
}