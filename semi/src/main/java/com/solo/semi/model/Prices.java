package com.solo.semi.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PRICES")
@ToString
public class Prices {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int pnum;
	
	private String coincode;
	
	private Double price;
	
	@Column(name = "volumes")
	private Double volume;
	
	@Column(name = "dates")
	private LocalDateTime date;
}
