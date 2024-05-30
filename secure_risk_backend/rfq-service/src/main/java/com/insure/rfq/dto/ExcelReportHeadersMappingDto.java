package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcelReportHeadersMappingDto {
	
	@JsonProperty(value = "aliasName")
	private String aliasName;
	@JsonProperty(value = "createdDate")
	private String createdDate;
	@JsonProperty(value = "updatedDate")
	private String updatedDate;
	@JsonProperty(value = "status")
	private String status;
}
