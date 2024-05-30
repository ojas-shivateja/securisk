package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimsMisDataStatusValidateDto {
	@JsonProperty(value = "sno")
	private boolean sno;
	@JsonProperty(value = "policyNumberStatus")
	private boolean policyNumberStatus;
	private String policyNumber;
	private String policyNumberErrorMessage;

	@JsonProperty(value = "claimsNumberStatus")
	private boolean claimsNumberStatus;
	private String claimsNumber;
	private String claimsNumberErrorMessage;

	@JsonProperty(value = "employeeIdStatus")
	private boolean employeeIdStatus;
	private String employeeId;
	private String employeeIdErrorMessage;

	@JsonProperty(value = "employeeNameStatus")
	private boolean employeeNameStatus;
	private String employeeName;
	private String employeeNameErrorMessage;

	@JsonProperty(value = "relationshipStatus")
	private boolean relationshipStatus;
	private String relationship;
	private String relationshipErrorMessage;

	@JsonProperty(value = "genderStatus")
	private boolean genderStatus;
	private String gender;
	private String genderErrorMessage;

	@JsonProperty(value = "ageStatus")
	private boolean ageStatus;
	private String age;
	private String ageErrorMessage;

	@JsonProperty(value = "patientNameStatus")
	private boolean patientNameStatus;
	private String patientName;
	private String patientNameErrorMessage;

	@JsonProperty(value = "sumInsuredStatus")
	private boolean sumInsuredStatus;
	private String sumInsured;
	private String sumInsuredErrorMessage;

	@JsonProperty(value = "claimedAmountStatus")
	private boolean claimedAmountStatus;
	private String claimedAmount;
	private String claimedAmountErrorMessage;

	@JsonProperty(value = "paidAmountStatus")
	private boolean paidAmountStatus;
	private String paidAmount;
	private String paidAmountErrorMessage;
	
	@JsonProperty(value = "outstandingAmountStatus")
	private boolean outstandingAmountStatus;
	private String outstandingAmount;
	private String outstandingAmountErrorMessage;

	@JsonProperty(value = "dateOfClaimStatus")
	private boolean dateOfClaimStatus;
	private String dateOfClaim;
	private String dateOfClaimErrorMessage;
	
	@JsonProperty(value = "claimStatus")
	private boolean claimStatus;
	private String claimStatusValue;
	private String claimStatusErrorMessage;

	@JsonProperty(value = "claimTypeStatus")
	private boolean claimTypeStatus;
	private String claimType;
	private String claimTypeErrorMessage;

	@JsonProperty(value = "status")
	private boolean status;
	private String statusErrorMessage;

	@JsonProperty(value = "networkTypeStatus")
	private boolean networkTypeStatus;
	private String networkType;
	private String networkTypeErrorMessage;

	@JsonProperty(value = "hospitalNameStatus")
	private boolean hospitalNameStatus;
	private String hospitalName;
	private String hospitalNameErrorMessage;

	@JsonProperty(value = "admissionDateStatus")
	private boolean admissionDateStatus;
	private String admissionDate;
	private String admissionDateErrorMessage;
	
	@JsonProperty(value = "dischargeDateStatus")
	private boolean dischargeDateStatus;
	private String dischargeDate;
	private String dischargeDateErrorMessage;

	@JsonProperty(value = "diseaseStatus")
	private boolean diseaseStatus;
	private String disease;
	private String diseaseErrorMessage;
	
	@JsonProperty(value = "memberCodeStatus")
	private boolean memberCodeStatus;
	private String memberCode;
	private String memberCodeErrorMessage;
	
	@JsonProperty(value = "policyStartDateStatus")
	private boolean policyStartDateStatus;
	private String policyStartDate;
	private String policyStartDateErrorMessage;
	
	@JsonProperty(value = "policyEndDateStatus")
	private boolean policyEndDateStatus;
	private String policyEndDate;
	private String policyEndDateErrorMessage;
	
	@JsonProperty(value = "hospitalStateStatus")
	private boolean hospitalStateStatus;
	private String hospitalState;
	private String hospitalStateErrorMessage;
	
	@JsonProperty(value = "hospitalCityStatus")
	private boolean hospitalCityStatus;
	private String hospitalCity;
	private String hospitalCityErrorMessage;
	
	@JsonProperty(value = "remarks")
	private String remarks;

}
