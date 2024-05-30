package com.insure.rfq.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpDepedentDto {
	
	@JsonProperty(value = "MEMBERNAME")
	private String memberName;
	
	@JsonProperty(value = "RELATION")
	private String relation;
	
	@JsonProperty(value = "GENDER")
	private String gender;
	
	@JsonProperty(value = "DOB")
	private LocalDateTime dob;
	
	@JsonProperty(value = "AGE")
	private int age;
	
	@JsonProperty(value = "SUMINSURED")
	private double sumInsured;
	
	@JsonProperty(value = "CREATEDDATE")
    private LocalDateTime createdDate;

    @JsonProperty(value = "UPDATEDDATE")
    private LocalDateTime updatedDate;

    @JsonProperty(value = "STATUS")
    private boolean status;
}
