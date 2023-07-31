package com.solo.semi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solo.semi.model.SiteUser;

import java.util.Optional;


public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByusername(String username);
}
