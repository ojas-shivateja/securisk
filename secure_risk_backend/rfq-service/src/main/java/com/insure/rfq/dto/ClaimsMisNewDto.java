package com.insure.rfq.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ClaimsMisNewDto {

	private String PolicyNumber;
	private String claimsNumber;
	private String employeeId;
	private String employeeName;
	private String relationship;
	private String gender;

	private int age;

	private String patientName;

	private double sumInsured;

	private double claimedAmount;

	private double paidAmount;

	private double outstandingAmount;

	private String claimStatus;

	private String dateOfClaim;

	private String claimType;

	private String networkType;

	private String hospitalName;

	private String admissionDate;

	private String disease;

	private String dateOfDischarge;

	private String memberCode;

	private String policyStartDate;

	private String policyEndDate;

	private String hospitalState;

	private String hospitalCity;

	private Date createDate;

	private Date updateDate;

	private String recordStatus;

}
