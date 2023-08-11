package com.semi.project.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @Column(name = "PNUM")
    private Long pnum;

	@Column(name = "COINCODE")
    private String coincode;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "VOLUMES")
    private Double volume;

    @Column(name = "DATES")
    private LocalDateTime date;

}
