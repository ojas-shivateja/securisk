package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcelReportHeadersDto {
	@JsonProperty(value = "headerName")
	private String headerName;
	@JsonProperty(value = "headerCategory")
	private String headerCategory;
	@JsonProperty(value = "headerAliasname")
	private ExcelReportHeadersMappingDto headerAliasname;
	@JsonProperty(value = "CREATEDDATE")
	private String createdDate;
	@JsonProperty(value = "UPDATEDDATE")
	private String updatedDate;
	@JsonProperty(value = "STATUS")
	private String status;
}
