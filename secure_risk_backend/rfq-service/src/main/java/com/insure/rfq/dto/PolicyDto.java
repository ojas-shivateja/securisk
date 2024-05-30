package com.insure.rfq.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDto {

	// --------- Overview data
	private String policyName;
	private String policyNumber;
	private String policyStartDate;
	private String policyEndDate;
	private MultipartFile policyCopyPath;
	private MultipartFile PPTPath;

	// --------- PolicyPresentation
	private String insuranceBroker;
	private String insuranceCompany;
	private String nameOfTheTPA;
	private Double inception_Premium;
	private String tillDatePremium;
	private String policyType;

	private String familyDefination;
	// _________________ IDperpes

}