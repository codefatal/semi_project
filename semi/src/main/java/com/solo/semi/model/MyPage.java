package com.solo.semi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "MYPAGE")
@Entity
public class MyPage {
	
    @Id
	@Column(name = "USERNAME")
    private String username;
	
    @Column(name = "MONEY")
    private Double money;

    @Column(name = "USER_BTC")
    private Double userBtc;

    @Column(name = "USER_ETH")
    private Double userEth;
    
    @Transient
    private double price;
    
}
