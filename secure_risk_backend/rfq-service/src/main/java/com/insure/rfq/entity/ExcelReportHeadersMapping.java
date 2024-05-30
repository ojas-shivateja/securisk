package com.insure.rfq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "EXCEL_REPORT_HEADERS_MAPPING")
@Data
public class ExcelReportHeadersMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ALIASNAME")
	private String aliasName;
	@Column(name = "CREATEDDATE")
	private String createdDate;
	@Column(name = "UPDATEDDATE")
	private String updatedDate;
	@Column(name = "STATUS")
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "HEADERID")
	@JsonIgnore
	@ToString.Exclude
	private ExcelReportHeaders reportHeaders;
}
