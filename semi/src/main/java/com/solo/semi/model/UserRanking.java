package com.solo.semi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRanking {
	private String username;
    private Double evaluationAmount;
    private Double profitLoss;
    private Double profitRate;
    private Double userBtc;
    private Double userEth;
    private Double sumBtcTradePrice;
    private Double sumEthTradePrice;
    
    public UserRanking() {}
    
    public UserRanking(String username, Double evaluationAmount, Double profitLoss, Double profitRate) {
    	this.username = username;
        this.evaluationAmount = evaluationAmount;
        this.profitLoss = profitLoss;
        this.profitRate = profitRate;
    }
}
