package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CorporateDetailsDto {

	@JsonProperty(value = "rfqId")
	private String rfqId;
	
	//RFQ Details
	@JsonProperty(value = "prodCategoryId")
	private Long prodCategoryId;

	@JsonProperty(value = "productId")
	private Long productId;
	
	@JsonProperty(value = "productCategory")
	private String productCategory;
	
	@JsonProperty(value = "product")
	private String product;

	@JsonProperty(value = "policyType")
	private String policyType;
	
	//Corporate Details
	@NotBlank
	@JsonProperty(value = "insuredName")
	private String insuredName;

	@NotBlank
	@JsonProperty(value = "address")
	private String address;

	@NotBlank
	@JsonProperty(value = "nob")
	private String nob;
	@JsonProperty(value = "nobCustom")
	private  String nobCustom;
	
	@NotBlank
	@JsonProperty(value = "contactName")
	private String contactName;

	@NotBlank
	@Email
	@JsonProperty(value = "email")
	private String email;

	@NotBlank
	@JsonProperty(value = "phNo")
	@Pattern(regexp="(^$|[0-9]{10})")
	private String phNo;
	
	//Intermediary Details
	@NotBlank
	@JsonProperty(value = "intermediaryName")
	private String intermediaryName;

	@NotBlank
	@JsonProperty(value = "intermediaryContactName")
	private String intermediaryContactName;

	@NotBlank
	@Email
	@JsonProperty(value = "intermediaryEmail")
	private String intermediaryEmail;

	@NotBlank
	@Size(min = 10, max = 10)
	@JsonProperty(value = "intermediaryPhNo")
	@Pattern(regexp="(^$|[0-9]{10})")
	private String intermediaryPhNo;

	//TPA Details
//	@NotBlank
	@JsonProperty(value = "tpaName")
	private String tpaName;

//	@NotBlank
	@JsonProperty(value = "tpaContactName")
	private String tpaContactName;

//	@NotBlank
//	@Email
	@JsonProperty(value = "tpaEmail")
	private String tpaEmail;

//	@NotBlank
//	@Size(min = 10, max = 10)
//	@Pattern(regexp="(^$|[0-9]{10})")
	@JsonProperty(value = "tpaPhNo")
	private String tpaPhNo;

//	@NotBlank
	@JsonProperty(value = "createDate")
	private String createDate;

//	@NotBlank
	@JsonProperty(value = "updateDate")
	private String updateDate;

//	@NotBlank
	@JsonProperty(value = "recordStatus")
	private String recordStatus;
	
	@JsonProperty(value = "appStatus")
	private String appStatus;
	
	@JsonProperty(value = "location")
	private String location;
}
