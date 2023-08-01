package com.solo.semi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "COINS")
@ToString
public class Coins {
	
	@Id
    @Column(name = "COINCODE")
    private String coincode;

    @Column(name = "COINNAME")
    private String coinname;
    
}
