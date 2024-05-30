package com.insure.rfq.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetClientListPolicyDto {

	// --------- Overview data

	private Long id;
	private String policyName;
	private String policyNumber;
	private String policyStartDate;
	private String policyEndDate;
	private String policyCopyPath;
	private String PPTPath;
	private String policyType;

	// --------- PolicyPresentation
	private String insuranceBroker;
	private String insuranceCompany;
	private String nameOfTheTPA;
	private Double inception_Premium;
	private String tillDatePremium;
	private List<GetChildClientListPolicyDto> familyDefination;

	// _________________ IDperpes

	private String rfqId;

	private String productid;

	private String clientId;
}