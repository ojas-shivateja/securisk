package com.insure.rfq.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TpaDto {

	@JsonProperty(value = "tpaId")
	private Long tpaId;

	@JsonProperty(value = "tpaName")
	private String tpaName;

	@JsonProperty(value = "location")
	private String location;
	
	@JsonProperty(value = "recordStatus")
	private String recordStatus;
	
	@JsonProperty(value = "tpaHeaders")
	private List<ClaimsTPAHeadersDto> tpaHeaders;
}
