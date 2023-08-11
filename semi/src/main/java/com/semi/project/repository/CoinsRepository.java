package com.semi.project.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.semi.project.model.Coins;

@Repository
public interface CoinsRepository extends CrudRepository<Coins, String>{
	
}
