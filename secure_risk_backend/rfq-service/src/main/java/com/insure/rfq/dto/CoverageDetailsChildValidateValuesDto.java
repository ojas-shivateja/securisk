package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverageDetailsChildValidateValuesDto {

	@JsonProperty(value = "employeeIdValidationStatus")
	private boolean employeeIdValidationStatus;
	
	@JsonProperty(value = "employeeIdValue")
	private String employeeIdValue;

	@JsonProperty(value = "employeeIdErrorMessage")
	private String employeeIdErrorMessage;

	@JsonProperty(value = "employeeNameValidationStatus")
	private boolean employeeNameValidationStatus;
	
	@JsonProperty(value = "employeeNameValue")
	private String employeeNameValue;

	@JsonProperty(value = "employeeNameErrorMessage")
	private String employeeNameErrorMessage;

	@JsonProperty(value = "relationshipValidationStatus")
	private boolean relationshipValidationStatus;
	
	@JsonProperty(value = "relationshipValue")
	private String relationshipValue;

	@JsonProperty(value = "relationshipErrorMessage")
	private String relationshipErrorMessage;

	@JsonProperty(value = "genderValidationStatus")
	private boolean genderValidationStatus;
	
	@JsonProperty(value = "genderValue")
	private String genderValue;

	@JsonProperty(value = "genderErrorMessage")
	private String genderErrorMessage;
	
	@JsonProperty(value = "ageValidationStatus")
	private boolean ageValidationStatus;
	
	@JsonProperty(value = "ageValue")
	private String ageValue;

	@JsonProperty(value = "ageErrorMessage")
	private String ageErrorMessage;

	@JsonProperty(value = "dateOfBirthValidationStatus")
	private boolean dateOfBirthValidationStatus;
	
	@JsonProperty(value = "dateOfBirthValue")
	private String dateOfBirthValue;

	@JsonProperty(value = "dateOfBirthErrorMessage")
	private String dateOfBirthErrorMessage;

	@JsonProperty(value = "sumInsuredValidationStatus")
	private boolean sumInsuredValidationStatus;
	
	@JsonProperty(value = "sumInsuredValue")
	private String sumInsuredValue;
	
	@JsonProperty(value = "sumInsuredErrorMessage")
	private String sumInsuredErrorMessage;
	
	@JsonProperty(value = "remarks")
	private String remarks;

}
