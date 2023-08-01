package com.solo.semi.service;

import com.solo.semi.model.SiteUser;
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
    private final PasswordEncoder passwordEncoder;
    private final MyPageService myPageService;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        
        // MyPageService를 호출하여 해당 사용자와 연결된 MyPage 엔티티를 생성하고 저장
        myPageService.createMyPageForUser(user);
        
        return user;
    }
}
