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
@Table(name = "CoverageValidateFilenames")
@Data
public class CoverageValidateFilenames {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "SNo")
	private String sno;
	
	@Column(name = "EMPLOYEEID")
	private String EMPLOYEEID;
	
	@Column(name = "EMPLOYEENAME")
	private String EMPLOYEENAME;
	
	@Column(name = "RELATIONSHIP")
	private String RELATIONSHIP;
	
	@Column(name = "GENDER")
	private String GENDER;
	
	@Column(name = "DATEOFBIRTH")
	private String DATEOFBIRTH;
	
	@Column(name = "AGE")
	private String AGE;
	
	@Column(name = "SUMINSURED")
	private String SUMINSURED;
	
	
	
	@Column(name = "CREATEDATE")
	private Date createDate;
	
	@Column(name = "UPDATEDATE")
	private Date updateDate;
	
	@Column(name = "RECORDSTATUS")
	private String recordStatus;
}
