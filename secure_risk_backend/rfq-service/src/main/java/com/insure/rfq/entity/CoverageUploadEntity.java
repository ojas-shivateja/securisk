package com.insure.rfq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "COVERAGE_UPLOAD_ENTITY")
@Data
public class CoverageUploadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "EMPLOYEEDEPDENENTDATA")
	private byte[] EmployeeDepdenentData;

	@Column(name = "MANDATELETTER")
	private byte[] MandateLetter;

	@Column(name = "COVERAGESSOUGHT")
	private byte[] CoveragesSought;

	@Column(name = "CLAIMSMIS")
	private byte[] ClaimsMis;

	@Column(name = "CLAIMSSUMMARY")
	private byte[] ClaimsSummary;

	@Column(name = "FILENAME")
	private String fileName;
}
