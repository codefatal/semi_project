package com.solo.semi.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.Prices;

@Repository
public interface PricesRepository extends CrudRepository<Prices, Integer>{
	List<Prices> findFirst10ByCoincodeOrderbyDateDesc(String Coincode);
	
	Long countByCoincode(String Coincode); // 코인별 데이터 수 조회 
}
