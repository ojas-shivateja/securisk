package com.insure.rfq.entity;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ExpiryPolicy_Details")
@Data
public class ExpiryPolicyDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private UUID id;

	@Column(name = "RFQ_ID")
	private String rfqId;

	//Policy Details
	@Column(name = "POLICY_NUMBER")
	private String policyNumber;

	@Column(name = "START_PERIOD")
	private Date startPeriod;

	@Column(name = "END_PERIOD")
	private Date endPeriod;

	// premium
	@Column(name = "PREMIUMPAID_INCEPTION")
	private String premiumPaidInception;

	@Column(name = "PREMIUM")
	private String premium;

	@Column(name = "ADDITION_PREMIUM")
	private String additionPremium;

	@Column(name = "DELETION_PREMIUM")
	private String deletionPremium;

	@Column(name = "POLICY_TYPE")
	private String policyType;

	@Column(name = "ACTIVE_YEARS")
	private String activeYears;

	// member
	@Column(name = "MEMBERSNO_INCEPTION")
	private String membersNoInception;

	@Column(name = "ADDITIONS")
	private String additions;

	@Column(name = "DELETIONS")
	private String deletions;

	@Column(name = "TOTAL_MEMBERS")
	private String totalMembers;

	//members dependent
	@Column(name = "MEMBERSNO_INCEPTION_DEPENDENT")
	private String membersNoInceptionForDependents;

	@Column(name = "ADDITIONS_DEPENDENT")
	private String additionsForDependents;

	@Column(name = "DELETIONS_DEPENDENT")
	private String deletionsForDependents;

	@Column(name = "TOTAL_MEMBERS_DEPENDENT")
	private String totalMembersForDependents;

	// renewal
	@Column(name = "MEMBERS_NUM")
	private String membersNum;

	@Column(name = "DEPENDENT_MEMBER")
	private String dependentMember;

	@Column(name = "FAMILY_DEFINATION")
	private String familyDefination;

	@Column(name = "FAMILIES_NUM")
	private String familiesNum;
	
	@Column(name = "ADDITIONAL_RELATIONSHIP")
	private String additionalRelationShip;
	
	@Column(name = "FAMILYDEFINATION_REVISION")
	private String familyDefinationRevision;

	@Column(name = "CREATED_DATE")
	private String createDate;

	@Column(name = "UPDATED_DATE")
	private String updateDate;

	@Column(name = "RECORD_STATUS")
	private String recordStatus;
}
