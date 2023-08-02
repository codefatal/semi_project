package com.solo.semi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "MYPAGE")
@Entity
public class MyPage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
	
    @Column(name = "MONEY")
    private Double money;

    @Column(name = "USER_BTC")
    private Double userBtc;

    @Column(name = "USER_ETH")
    private Double userEth;
    
}
