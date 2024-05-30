package com.insure.rfq.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "INTERMEDIARY_DETAILS")
@Data
public class IntermediaryDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INTERMEDIARYID")
	private Long intermediaryId;
	
	@Column(name = "PRODUCT")
	private String Product;
	@Column(name = "PRODUCTCATEGORY")
	private String ProductCategory;
	@Column(name = "COVERAGECOUNT")
	private int coverageCount;
	
	@Column(name = "CREATEDDATE")
	private Date createdDate;
	@Column(name = "UPDATEDATE")
	private Date updateDate;
	@Column(name = "RECORDSTATUS")
	private String recordStatus;
}
