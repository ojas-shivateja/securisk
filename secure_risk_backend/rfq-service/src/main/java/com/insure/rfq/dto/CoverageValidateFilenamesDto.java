package com.insure.rfq.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverageValidateFilenamesDto {
	
	@JsonProperty(value = "sno")
	private String sno;
	
	@JsonProperty(value = "employeeId")
	private String employeeId;

	@JsonProperty(value = "employeeName")
	private String employeeName;

	@JsonProperty(value = "relationship")
	private String relationship;

	@JsonProperty(value = "gender")
	private String gender;

	@JsonProperty(value = "dateOfBirth")
	private String dateOfBirth;

	@JsonProperty(value = "age")
	private String age;

	@JsonProperty(value = "sumInsured")
	private String sumInsured;

	@JsonProperty(value = "createDate")
	private Date createDate;

	@JsonProperty(value = "updateDate")
	private Date updateDate;

	@JsonProperty(value = "recordStatus")
	private String recordStatus;
}
