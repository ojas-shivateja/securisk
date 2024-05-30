package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class PolicyTermsChildDto {
	
	@JsonProperty(value = "policyTermId")
	private UUID policyTermId;
	
//	@NotBlank
	@JsonProperty(value = "coverageName")
	private String coverageName;
	
//	@NotBlank
	@JsonProperty(value = "remark")
	private String remark;
}
