package com.solo.semi.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.MyPage;
import com.solo.semi.model.TradeTest;

@Repository
public interface TradeTestRepository extends CrudRepository<TradeTest, String> {
	@Query("SELECT SUM(tt.tradePrice) FROM TradeTest tt WHERE tt.username = :username AND tt.tradeType = 1")
	Double sumTradePriceByUsernameAndTradeType(@Param("username") String username);
}
