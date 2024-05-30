package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllRFQDetailsDto {
	@JsonProperty(value = "rfqId")
	private String rfqId;
	@JsonProperty(value = "product")
	private String product;
	@JsonProperty(value = "productCategory")
	private String productCategory;
	@JsonProperty(value = "policyType")
	private String policyType;
	@JsonProperty(value = "insurerName")
	private String insurerName;
	@JsonProperty(value = "nob")
	private String nob;
	@JsonProperty(value = "phNo")
	private String phNo;
	@JsonProperty(value = "email")
	private String email;
	//Policy Id is not mandatory
	@JsonProperty(value = "policyId")
	private String policyId;
	@JsonProperty(value = "appStatus")
	private String appStatus;
	@JsonProperty(value = "createDate")
	private String createDate;
	
}
