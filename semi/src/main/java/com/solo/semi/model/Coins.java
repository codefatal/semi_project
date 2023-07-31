package com.solo.semi.model;

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
	private String coincode;
	
	private String coinname;
}
