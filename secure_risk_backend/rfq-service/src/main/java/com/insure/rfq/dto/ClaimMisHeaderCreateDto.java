package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ClaimMisHeaderCreateDto {
	@JsonProperty(value = "headerName")
	private String headerName;
	@JsonProperty(value = "headerCategory")
	private String headerCategory;
	@JsonProperty(value = "headerAliasName")
	private String headerAliasName;
}
