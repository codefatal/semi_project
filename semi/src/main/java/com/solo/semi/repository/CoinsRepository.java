package com.solo.semi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.Coins;

@Repository
public interface CoinsRepository extends CrudRepository<Coins, String>{
	
}
