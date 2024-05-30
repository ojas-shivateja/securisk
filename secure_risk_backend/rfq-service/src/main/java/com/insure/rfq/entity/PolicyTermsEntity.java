package com.insure.rfq.entity;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PolicyTerms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyTermsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "RFQ_ID")
	private String rfqId;
	
	@Column(name = "COVERAGE_NAME")
	private String coverageName;
	
	@Column(name = "REMARK")
	private String remark;
	
	@Column(name = "CREATED_DATE")
	private Date createDate;

	@Column(name = "UPDATED_DATE")
	private Date updateDate;

	@Column(name = "RECORD_STATUS")
	private String recordStatus;
	
}
