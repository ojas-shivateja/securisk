package com.insure.rfq.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClaimAnalysisDto {
	private Long id;
	private String rfqId;
	private String policyNumber;
	private String employeeId;
	private String employeeName;
	private int age;
	private String genderBeneficiary;
	private String relationShip;
	private double sumInsured;
	private String gender;

	// Claim Details of Employee or its family Member
	private String claimNumber;
	private String claimType;
	private String networkType;
	private double claimedAmount;
	private double paidAmount;
	private Date claimDate;
	private String type;
	private String status;

	// Medical Details
	private String hospitalName;
	private String locationBilled;
	private String admissionDate;
	private String disease;
	private String treatment;
	private String category;

}
