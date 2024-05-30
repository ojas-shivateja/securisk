package com.insure.rfq.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COVERAGE_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverageDetailsEntity {
	
	@Id
	@SequenceGenerator(name = "mySeq",sequenceName = "seq", allocationSize = 1)
	@GeneratedValue(generator = "mySeq", strategy = GenerationType.SEQUENCE)
	private Long coveragedId;
	
	@Column(name = "RFQ_ID")
	private String rfqId;
	
	@Column(name = "POLICY_TYPE")
	private String policyType;

	@Column(name = "FAMILY_DEFINATION")
	private String familyDefination;
	
	@Column(name = "SUMINSURED")
	private String sumInsured;
	
	@Column(name = "FAMILYDEFICATION_13")
	private boolean familyDefication13;
	
	@Column(name = "FAMILYDEFICATION_15")
	private boolean familyDefication15;
	
	@Column(name = "FAMILYDEFICATION_PARENTS")
	private boolean familyDeficationParents;
	
	@Column(name = "FAMILYDEFICATION_13_AMOUNT")
	private List<Double> familyDefication13Amount;
	
	@Column(name = "FAMILYDEFICATION_15_AMOUNT")
	private List<Double> familyDefication15Amount;
	
	@Column(name = "FAMILYDEFICATION_PARENTS_AMOUNT")
	private List<Double> familyDeficationParentsAmount;
	
	@Column(name = "ISEMP_DATA")
	private boolean isEmpData;
	
	@Column(name = "EMPDEPDATA_FILEPATH")
	String empDepDataFilePath;
	
	@Column(name = "MANDATELETTER_FILEPATH")
	String mandateLetterFilePath;
	
	@Column(name = "COVERAGES_FILEPATH")
	String coveragesFilePath;
	
	@Column(name = "CLAIMSMISC_FILEPATH")
	String claimsMiscFilePath;
	
	@Column(name = "CLAIMSSUMMARY_FILEPATH")
	String claimsSummaryFilePath;
	
	@Column(name = "TEMPLATE_FILEPATH")
	String templateFilePath;
	
	@Column(name = "POLICYCOPY_FILEPATH")
	String policyCopyFilePath;

	@Column(name = "UPLOADED_DOCUMENTS_PATH")
	String uploadedDocumentsPath;
	
	@Column(name = "CREATED_DATE")
	private Date createDate;

	@Column(name = "UPDATED_DATE")
	private Date updateDate;

	@Column(name = "RECORD_STATUS")
	private String recordStatus;
}
