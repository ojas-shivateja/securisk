package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CovergaeHeaderValidateDto {
	
	@JsonProperty(value = "sno")
	private boolean sno;
	
	@JsonProperty(value = "policyNumberStatus")
	private boolean policyNumberStatus;
	
	@JsonProperty(value = "claimsNumberStatus")
	private boolean claimsNumberStatus;
	
	@JsonProperty(value = "employeeIdStatus")
	private boolean employeeIdStatus;
	
	@JsonProperty(value = "employeeNameStatus")
	private boolean employeeNameStatus;
	
	@JsonProperty(value = "relationshipStatus")
	private boolean relationshipStatus;
	
	@JsonProperty(value = "genderStatus")
	private boolean genderStatus;
	
	@JsonProperty(value = "ageStatus")
	private boolean ageStatus;
	
	@JsonProperty(value = "patientNameStatus")
	private boolean patientNameStatus;
	
	@JsonProperty(value = "sumInsuredStatus")
	private boolean sumInsuredStatus;
	
	@JsonProperty(value = "claimedAmountStatus")
	private boolean claimedAmountStatus;
	
	@JsonProperty(value = "paidAmountStatus")
	private boolean paidAmountStatus;
	
	@JsonProperty(value = "outstandingAmountStatus")
	private boolean outstandingAmountStatus;
	
	@JsonProperty(value = "claimStatus")
	private boolean claimStatus;
	
	@JsonProperty(value = "dateOfClaimStatus")
	private boolean dateOfClaimStatus;
	
	@JsonProperty(value = "claimTypeStatus")
	private boolean claimTypeStatus;
	
	@JsonProperty(value = "status")
	private boolean status;
	
	@JsonProperty(value = "networkTypeStatus")
	private boolean networkTypeStatus;
	
	@JsonProperty(value = "hospitalNameStatus")
	private boolean hospitalNameStatus;
		
	@JsonProperty(value = "admissionDateStatus")
	private boolean admissionDateStatus;
	
	@JsonProperty(value = "dischargeDateStatus")
	private boolean dischargeDateStatus;
	
	@JsonProperty(value = "diseaseStatus")
	private boolean diseaseStatus;
	
	@JsonProperty(value = "memberCodeStatus")
	private boolean memberCodeStatus;
	
	@JsonProperty(value = "policyStartDateStatus")
	private boolean policyStartDateStatus;
	
	@JsonProperty(value = "policyEndDateStatus")
	private boolean policyEndDateStatus;
	
	@JsonProperty(value = "hospitalStateStatus")
	private boolean hospitalStateStatus;
	
	@JsonProperty(value = "hospitalCityStatus")
	private boolean hospitalCityStatus;
	
}
