package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimsTPAHeadersDto {

	@JsonProperty(value = "headerName")
	private String headerName;

	@JsonProperty(value = "headerAliasName")
	private String headerAliasName;

	@JsonProperty(value = "sheetName")
	private String sheetName;

}
