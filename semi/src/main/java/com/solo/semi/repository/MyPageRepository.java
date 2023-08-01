package com.solo.semi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.MyPage;

@Repository
public interface MyPageRepository extends CrudRepository<MyPage, Long>{
	
}
