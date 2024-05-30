package com.insure.rfq.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoverageDetailsDto {
	@JsonProperty(value = "rfqId")
	private String rfqId;
	@JsonProperty(value ="empDepDataFilePath") 
	String empDepDataFilePath;
	@JsonProperty(value ="mandateLetterFilePath") 
	String mandateLetterFilePath;
	@JsonProperty(value ="coveragesFilePath") 
	String coveragesFilePath;
	@JsonProperty(value ="claimsMiscFilePath") 
	String claimsMiscFilePath;
	@JsonProperty(value ="claimsSummaryFilePath") 
	String claimsSummaryFilePath;
	@JsonProperty(value ="templateFilePath") 
	String templateFilePath;
	@JsonProperty(value ="policyCopyFilePath") 
	String policyCopyFilePath;
	@JsonProperty(value = "uploadedDocumentsPath")
	String uploadedDocumentsPath;

	@NotNull
	@JsonProperty(value = "policyType")
	private String policyType;

	@NotNull
	@JsonProperty(value = "familyDefination")
	private String familyDefination;

	@NotNull
	@JsonProperty(value = "sumInsured")
	private String sumInsured;

	@JsonProperty(value = "familyDefication13")
	private boolean familyDefication13;

	@JsonProperty(value = "familyDefication15")
	private boolean familyDefication15;

	@JsonProperty(value = "familyDeficationParents")
	private boolean familyDeficationParents;

	@JsonProperty(value = "familyDefication13Amount")
	private List<Double> familyDefication13Amount;

	@JsonProperty(value = "familyDefication15Amount")
	private List<Double> familyDefication15Amount;

	@JsonProperty(value = "familyDeficationParentsAmount")
	private List<Double> familyDeficationParentsAmount;

	@JsonProperty(value = "empData")
	private boolean empData;

	@JsonProperty(value = "createDate")
	private Date createDate;

	@JsonProperty(value = "updateDate")
	private Date updateDate;

	@JsonProperty(value = "recordStatus")
	private String recordStatus;

}
