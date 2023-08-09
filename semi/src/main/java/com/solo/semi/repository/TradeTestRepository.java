package com.solo.semi.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.solo.semi.model.TradeTest;

@Repository
public interface TradeTestRepository extends CrudRepository<TradeTest, String> {
	Optional<TradeTest> findByUsername(String currentUsername);
	
	Optional<TradeTest> findTopByUsernameOrderByDateDesc(String currentUsername);
	
	
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
	
	// 코인별 평균 매수금액
	@Query("SELECT nvl(avg(tradeCoinMoney),0) from TradeTest tt where tt.coincode = :coincode AND tt.username = :username AND tt.tradeType = 1")
	Double buyCoinAvg(@Param("coincode") String coincode, @Param("username") String username);
	
	@Query("SELECT tt FROM TradeTest tt WHERE tt.username = :username ORDER BY tt.date DESC")
	List<TradeTest> tradeList(@Param("username") String username);
	
	// 매수 거래내역
	@Query("SELECT tt FROM TradeTest tt WHERE tt.username = :username AND tt.tradeType = 1 ORDER BY tt.date DESC")
	List<TradeTest> tradeBuyList(@Param("username") String username);
	
	// 매도 거래내역
	@Query("SELECT tt FROM TradeTest tt WHERE tt.username = :username AND tt.tradeType = 2 ORDER BY tt.date DESC")
	List<TradeTest> tradeSellList(@Param("username") String username);
	
	// 랭킹 평가 손익 내림차순 계산
	@Query("SELECT tt.username, " +
		       "(COALESCE(SUM(CASE WHEN tt.tradeType = 1 THEN tt.tradePrice ELSE 0 END), 0)) AS buySum, " +
		       "(COALESCE(SUM(CASE WHEN tt.tradeType = 1 THEN tt.tradePrice ELSE 0 END), 0) - " +
		       " COALESCE(SUM(CASE WHEN tt.tradeType = 2 THEN tt.tradePrice ELSE 0 END), 0)) AS profitLoss, " +
		       "p.price AS currentPrice, " +
		       "(p.price * COALESCE(SUM(tt.tradeItem), 0)) - (COALESCE(SUM(CASE WHEN tt.tradeType = 1 THEN tt.tradePrice ELSE 0 END), 0)) AS evaluationProfitLoss " +
		       "FROM TradeTest tt " +
		       "JOIN Prices p ON p.coincode = tt.coincode " +  
		       "WHERE tt.coincode = :coincode " +
		       "AND p.date = (SELECT MAX(p2.date) FROM Prices p2 WHERE p2.coincode = :coincode) " + 
		       "GROUP BY tt.username, p.price " +
		       "ORDER BY evaluationProfitLoss DESC")  // 평가 손익을 기준으로 정렬
		List<Object[]> getRankingByCoin(@Param("coincode") String coincode);
}
