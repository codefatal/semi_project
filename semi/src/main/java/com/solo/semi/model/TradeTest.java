package com.solo.semi.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TradeTest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "USERNAME")
	private String username;

    @Column(name = "COINCODE")
    private String coincode;

    @Column(name = "TRADE_TYPE")
    private Integer tradeType;

    @Column(name = "TRADE_PRICE")
    private double tradePrice;

    @Column(name = "TRADE_ITEM")
    private Double tradeItem;
    
    @Column(name = "TRADE_COIN_MONEY")
    private Double tradeCoinMoney;

    @Column(name = "TRADE_DATE")
    private LocalDateTime date;
    
}
