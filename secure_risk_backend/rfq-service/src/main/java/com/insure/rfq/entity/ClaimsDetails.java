package com.insure.rfq.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CLAIM_DETAILS")
@Data
public class ClaimsDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long claimId;

	@Column(name = "RFQID")
	private String rfqId;
	@Column(name = "CLAIMPAIDREIMBURSEMENT")
	private String claimPaidReimbursement;
	@Column(name = "CLAIMPAIDCASHLESS")
	private String claimPaidCashless;
	@Column(name = "CLAIMOUTSTANDINGREIMBURSEMENT")
	private String claimOutstandingReimbursement;
	@Column(name = "CLAIMOUTSTANDINGCASHLESS")
	private String claimOutstandingCashless;

	@Column(name = "OPDCLAIMPAIDREIMBURSEMENT")
	private String opdClaimPaidReimbursement;
	@Column(name = "OPDCLAIMPAIDCASHLESS")
	private String opdClaimPaidCashless;
	@Column(name = "OPDCLAIMOUTSTANDINGREIMBURSEMENT")
	private String opdClaimOutstandingReimbursement;
	@Column(name = "OPDCLAIMOUTSTANDINGCASHLESS")
	private String opdClaimOutstandingCashless;

	@Column(name = "CORPORATEBUFFERCLAIMPAIDREIMBURSEMENT")
	private String corporateBufferClaimPaidReimbursement;
	@Column(name = "CORPORATEBUFFERCLAIMPAIDCASHLESS")
	private String corporateBufferClaimPaidCashless;
	@Column(name = "CORPORATEBUFFERCLAIMOUTSTANDINGREIMBURSEMENT")
	private String corporateBufferClaimOutstandingReimbursement;
	@Column(name = "CORPORATEBUFFERCLAIMOUTSTANDINGCASHLESS")
	private String corporateBufferClaimOutstandingCashless;

	@Column(name = "CORPORATEBUFFERAMOUNT")
	private String corporateBufferAmount;
	@Column(name = "PERFAMILYLIMIT")
	private String perFamilyLimit;
	@Column(name = "MAXCASESNO")
	private String maxCasesNo;
	@Column(name = "MAXSUMINSURED")
	private String maxSumInsured;
	
	//Non-GHI products
	@Column(name = "POLICYPERIOD")
	private String policyperiod;
	@Column(name = "PREMIUMPAID")
	private String premiumPaid;
	@Column(name = "TOTALSUMINSURED")
	private String totalSumInsured;
	@Column(name = "CLAIMSNO")
	private String claimsNo;
	@Column(name = "CLAIMEDAMOUNT")
	private String claimedAmount;
	@Column(name = "SETTLEDAMOUNT")
	private String settledAmount;
	@Column(name = "PENDINGAMOUNT")
	private String pendingAmount;

	@Column(name = "CREATEDATE")
	private Date createDate;
	@Column(name = "UPDATEDATE")
	private Date updateDate;
	@Column(name = "RECORDSTATUS")
	private String recordStatus;
}
