package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimDetailsDto {

	@JsonProperty(value = "rfqId")
	private String rfqId;
	@JsonProperty(value = "claimPaidReimbursement")
	private String claimPaidReimbursement;
	@JsonProperty(value = "claimPaidCashless")
	private String claimPaidCashless;
	@JsonProperty(value = "claimOutstandingReimbursement")
	private String claimOutstandingReimbursement;
	@JsonProperty(value = "claimOutstandingCashless")
	private String claimOutstandingCashless;

	@JsonProperty(value = "opdClaimPaidReimbursement")
	private String opdClaimPaidReimbursement;
	@JsonProperty(value = "opdClaimPaidCashless")
	private String opdClaimPaidCashless;
	@JsonProperty(value = "opdClaimOutstandingReimbursement")
	private String opdClaimOutstandingReimbursement;
	@JsonProperty(value = "opdClaimOutstandingCashless")
	private String opdClaimOutstandingCashless;

	@JsonProperty(value = "corporateBufferClaimPaidReimbursement")
	private String corporateBufferClaimPaidReimbursement;
	@JsonProperty(value = "corporateBufferClaimPaidCashless")
	private String corporateBufferClaimPaidCashless;
	@JsonProperty(value = "corporateBufferClaimOutstandingReimbursement")
	private String corporateBufferClaimOutstandingReimbursement;
	@JsonProperty(value = "corporateBufferClaimOutstandingCashless")
	private String corporateBufferClaimOutstandingCashless;

	@JsonProperty(value = "corporateBufferAmount")
	private String corporateBufferAmount;
	@JsonProperty(value = "perFamilyLimit")
	private String perFamilyLimit;
	@JsonProperty(value = "maxCasesNo")
	private String maxCasesNo;
	@JsonProperty(value = "maxSumInsured")
	private String maxSumInsured;

	// Non-GHI products
	@JsonProperty(value = "policyPeriod")
	private String policyPeriod;
	@JsonProperty(value = "premiumPaid")
	private String premiumPaid;
	@JsonProperty(value = "totalSumInsured")
	private String totalSumInsured;
	@JsonProperty(value = "claimsNo")
	private String claimsNo;
	@JsonProperty(value = "claimedAmount")
	private String claimedAmount;
	@JsonProperty(value = "settledAmount")
	private String settledAmount;
	@JsonProperty(value = "pendingAmount")
	private String pendingAmount;

//	@JsonProperty(value = "createDate")
	private String createDate;
//	@JsonProperty(value = "updateDate")
	private String updateDate;
	@JsonProperty(value = "recordStatus")
	private String recordStatus;
}
