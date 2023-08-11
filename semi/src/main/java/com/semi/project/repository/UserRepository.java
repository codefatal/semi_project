package com.semi.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semi.project.model.SiteUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SiteUser, String> {
	Optional<SiteUser> findByUsername(String username);
}
