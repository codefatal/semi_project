package com.solo.semi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TradeTest {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "COINCODE")
    private Coins coins;

    @Column(name = "TRADE_TYPE")
    private Integer tradeType;

    @Column(name = "TRADE_PRICE")
    private Integer tradePrice;

    @Column(name = "TRADE_ITEM")
    private Double tradeItem;

    @Column(name = "TRADE_MONEY")
    private Integer tradeMoney;

    @Column(name = "TRADE_COUNT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tradeCount;

    public String getCoincode() {
        if (coins != null) {
            return coins.getCoincode();
        }
        return null;
    }
    
    public void setCoincode(String coincode) {
        // Prices 엔티티의 coincode를 설정
        this.coins = new Coins(); // coins는 ManyToOne 관계이므로, 새로운 객체를 만들어서 설정해야 합니다.
        this.coins.setCoincode(coincode);
    }
}
