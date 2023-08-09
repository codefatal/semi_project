package com.solo.semi.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.MyPage;
import com.solo.semi.model.TradeTest;

@Repository
public interface TradeTestRepository extends CrudRepository<TradeTest, String> {
	
	//전체 계산
	@Query("SELECT (COALESCE(SUM(CASE WHEN tt.tradeType = 1 THEN tt.tradePrice ELSE 0 END), 0)"
			+ " - COALESCE(SUM(CASE WHEN tt.tradeType = 2 THEN tt.tradePrice ELSE 0 END), 0)) AS result"
			+ " FROM TradeTest tt"
			+ " WHERE tt.username = :username AND tt.tradeType IN (1, 2)")
	Double buySum(@Param("username") String username);

	// Coin별 계산
	@Query("SELECT (COALESCE(SUM(CASE WHEN tt.tradeType = 1 THEN tt.tradePrice ELSE 0 END), 0)"
			+ " - COALESCE(SUM(CASE WHEN tt.tradeType = 2 THEN tt.tradePrice ELSE 0 END), 0)) AS result"
			+ " FROM TradeTest tt"
			+ " WHERE tt.coincode = :coincode AND tt.username = :username AND tt.tradeType IN (1, 2)")
	Double buyCoinSum(@Param("coincode") String coincode, @Param("username") String username);
		
	@Query("SELECT nvl(avg(tradeCoinMoney),0) from TradeTest tt where tt.coincode = :coincode AND tt.username = :username AND tt.tradeType = 1")
	Double buyCoinAvg(@Param("coincode") String coincode, @Param("username") String username);
	
	@Query("SELECT tt FROM TradeTest tt WHERE tt.username = :username ORDER BY tt.date DESC")
	List<TradeTest> tradeList(@Param("username") String username);

	@Query("SELECT tt FROM TradeTest tt WHERE tt.username = :username AND tt.tradeType = 1 ORDER BY tt.date DESC")
	List<TradeTest> tradeBuyList(@Param("username") String username);
	
	@Query("SELECT tt FROM TradeTest tt WHERE tt.username = :username AND tt.tradeType = 2 ORDER BY tt.date DESC")
	List<TradeTest> tradeSellList(@Param("username") String username);
}
