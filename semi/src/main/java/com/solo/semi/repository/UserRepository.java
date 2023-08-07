package com.solo.semi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.SiteUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SiteUser, String> {
	Optional<SiteUser> findByUsername(String username);
}
