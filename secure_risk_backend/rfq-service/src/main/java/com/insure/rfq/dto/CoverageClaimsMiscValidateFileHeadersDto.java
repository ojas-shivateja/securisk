package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoverageClaimsMiscValidateFileHeadersDto {
	@JsonProperty(value = "POLICYNUMBER")
	private String policyNumber;
	@JsonProperty(value = "CLAIMSNO")
	private String claimsno;
	@JsonProperty(value = "EMPLOYEEID")
	private String employeeId;
	@JsonProperty(value = "EMPLOYEENAME")
	private String employeeName;
	@JsonProperty(value = "RELATIONSHIP")
	private String relationship;
	@JsonProperty(value = "RELATIONSHIP")
	private String gender;
	@JsonProperty(value = "AGE")
	private int age;
	@JsonProperty(value = "DATAOFBIRTH")
	private String dataOfBirth;
	@JsonProperty(value = "BENEFICIARYNAME")
	private String beneficiaryName;
	@JsonProperty(value = "SUMINSURED")
	private double sumInsured;
	@JsonProperty(value = "CLAIMEDAMOUNT")
	private double claimedAmount;
	@JsonProperty(value = "PAIDAMOUNT")
	private double paidAmount;
	@JsonProperty(value = "DATEOFCLAIM")
	private String dateOfClaim;
	@JsonProperty(value = "TYPE")
	private String type;
	@JsonProperty(value = "STATUS")
	private String status;
	@JsonProperty(value = "NETWORKTYPE")
	private String networkType;
	@JsonProperty(value = "HOSPITALNAME")
	private String hospitalName;
	@JsonProperty(value = "LOCATION")
	private String location;
	@JsonProperty(value = "BILLEDAMOUNT")
	private double billedAmount;
	@JsonProperty(value = "ADMISSIONDATE")
	private String admissionDate;
	@JsonProperty(value = "DISEASE")
	private String disease;
	@JsonProperty(value = "TREATMENT")
	private String treatment;
	@JsonProperty(value = "CATEGORY")
	private String category;
}
