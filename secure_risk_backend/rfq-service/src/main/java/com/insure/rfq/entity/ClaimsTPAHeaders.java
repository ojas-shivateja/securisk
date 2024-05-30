package com.insure.rfq.entity;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CLAIMS_TPA_HEADERS")
@Data
public class ClaimsTPAHeaders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TPAHEADERID")
	private int tpaHeaderid;
	
	@Column(name = "HEADERNAME")
	private String headerName;
	
	@Column(name = "HEADERALIASNAME")
	private String headerAliasName;
	
	@Column(name = "SHEETNAME")
	private String sheetName;

	@Column(name = "CREATEDDATE")
	private String createdDate;
	
	@Column(name = "UPDATEDDATE")
	private String updatedDate;
	
	@Column(name = "RECORDSTATUS")
	private String recordStatus;
	
	//referencedColumnName is based on database table not entity
	@ManyToOne
	@JsonManagedReference
	@JoinColumn(name = "tpa_id", referencedColumnName = "tpa_id")
	@ToStringExclude
	private Tpa tpaList;
	
}
