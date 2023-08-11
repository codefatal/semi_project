package com.semi.project.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.semi.project.model.Prices;

@Repository
public interface PricesRepository extends CrudRepository<Prices, Integer>{
	List<Prices> findFirst10ByCoincodeOrderByDateDesc(String coincode);
	
	Long countByCoincode(String coincode); // 코인별 데이터 수 조회 
	
	Prices findTopByCoincodeOrderByDateDesc(String coinCode);
}
