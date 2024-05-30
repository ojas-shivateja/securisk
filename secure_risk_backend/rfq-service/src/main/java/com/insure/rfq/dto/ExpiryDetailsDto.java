package com.insure.rfq.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExpiryDetailsDto {
	@JsonProperty(value = "rfqId")
	private String rfqId;

	// Policy Details
	@JsonProperty(value = "policyNumber")
	private String policyNumber;

	@JsonProperty(value = "startPeriod")
	private Date startPeriod;

	@JsonProperty(value = "endPeriod")
	private Date endPeriod;

	// premium
	@JsonProperty(value = "premiumPaidInception")
	private String premiumPaidInception;

	@JsonProperty(value = "premium")
	private String premium;

	@JsonProperty(value = "additionPremium")
	private String additionPremium;

	@JsonProperty(value = "deletionPremium")
	private String deletionPremium;

	@JsonProperty(value = "policyType")
	private String policyType;

	@JsonProperty(value = "activeYears")
	private String activeYears;

	// member
	@JsonProperty(value = "membersNoInception")
	private String membersNoInception;

	@JsonProperty(value = "additions")
	private String additions;

	@JsonProperty(value = "deletions")
	private String deletions;

	@JsonProperty(value = "totalMembers")
	private String totalMembers;

	// members dependent
	@JsonProperty(value = "membersNoInceptionForDependents")
	private String membersNoInceptionForDependents;

	@JsonProperty(value = "additionsForDependents")
	private String additionsForDependents;

	@JsonProperty(value = "deletionsForDependents")
	private String deletionsForDependents;

	@JsonProperty(value = "totalMembersForDependents")
	private String totalMembersForDependents;

	// renewal
	@JsonProperty(value = "membersNum")
	private String membersNum;

	@JsonProperty(value = "dependentMember")
	private String dependentMember;

	@JsonProperty(value = "familyDefination")
	private String familyDefination;

	@JsonProperty(value = "familiesNum")
	private String familiesNum;
	@JsonProperty(value = "additionalRelationShip")
	private String additionalRelationShip;

	@JsonProperty(value = "familyDefinationRevision")
	private String familyDefinationRevision;

	@JsonProperty(value = "createDate")
	private String createDate;

	@JsonProperty(value = "updateDate")
	private String updateDate;

	@JsonProperty(value = "recordStatus")
	private String recordStatus;
}
