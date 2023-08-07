package com.solo.semi.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.MyPage;

@Repository
public interface MyPageRepository extends CrudRepository<MyPage, String>{
	Optional<MyPage> findByUsername(String currentUsername);
	
}
