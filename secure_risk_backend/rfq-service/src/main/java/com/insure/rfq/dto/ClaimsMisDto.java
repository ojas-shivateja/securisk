package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimsMisDto {
	@JsonProperty(value = "PolicyNumber")
	private String policyNumber;
	@JsonProperty(value = "claimsNumber")
	private String claimsNumber;
	@JsonProperty(value = "employeeId")
	private String employeeId;
	@JsonProperty(value = "employeeName")
	private String employeeName;
	@JsonProperty(value = "relationship")
	private String relationship;
	@JsonProperty(value = "gender")
	private String gender;
	@JsonProperty(value = "age")
	private int age;
	@JsonProperty(value = "beneficiaryName")
	private String beneficiaryName;
	@JsonProperty(value = "sumInsured")
	private double sumInsured;
	@JsonProperty(value = "claimedAmount")
	private double claimedAmount;
	@JsonProperty(value = "paidAmount")
	private double paidAmount;
	@JsonProperty(value = "dateOfClaim")
	private String dateOfClaim;
	@JsonProperty(value = "type")
	private String type;
	@JsonProperty(value = "status")
	private String status;
	@JsonProperty(value = "networkType")
	private String networkType;
	@JsonProperty(value = "hospitalName")
	private String hospitalName;
	@JsonProperty(value = "location")
	private String location;
	@JsonProperty(value = "billedAmount")
	private double billedAmount;
	@JsonProperty(value = "admissionDate")
	private String admissionDate;
	@JsonProperty(value = "disease")
	private String disease;
	@JsonProperty(value = "treatment")
	private String treatment;
	@JsonProperty(value = "category")
	private String category;
}
