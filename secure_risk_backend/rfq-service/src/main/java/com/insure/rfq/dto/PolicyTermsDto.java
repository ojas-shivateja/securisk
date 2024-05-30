package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PolicyTermsDto {

//	@NotBlank
	@JsonProperty(value = "rfqId")
	private String rfqId;
	
	@Valid
	@JsonProperty(value = "policyDetails")
	private List<PolicyTermsChildDto> policyDetails;
	
	@JsonProperty(value= "createDate")
	private Date createDate;

	@JsonProperty(value= "updateDate")
	private Date updateDate;

	@JsonProperty(value= "recordStatus")
	private String recordStatus;
}
