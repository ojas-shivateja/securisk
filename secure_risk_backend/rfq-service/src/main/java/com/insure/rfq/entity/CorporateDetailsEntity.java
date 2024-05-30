package com.insure.rfq.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.insure.rfq.login.entity.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CORPORATE_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorporateDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	//RFQ Details
	@Column(name = "RFQ_ID")
	@GeneratedValue(generator = "custom-sequence-generator")
	private String rfqId;

	@Column(name = "PRODCATEGORY_ID")
	private Long prodCategoryId;

	@Column(name = "PRODUCT_ID")
	private Long productId;

	@Column(name = "POLICY_TYPE")
	private String policyType;


	//Corporate Details
	@Column(name = "INSURED_NAME")
	private String insuredName;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "NOB")
	private String nob;
	
	@Column(name = "nobCustom")
	private  String nobCustom;

	@Column(name = "CONTACTNAME")
	private String contactName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHNO")
	private String phNo;
	
	
	//Intermediary Details
	@Column(name = "INTERMEDIARY_NAME")
	private String intermediaryName;

	@Column(name = "INTERMEDIARY_CONTACTNAME")
	private String intermediaryContactName;

	@Column(name = "INTERMEDIARY_EMAIL")
	private String intermediaryEmail;

	@Column(name = "INTERMEDIARY_PHNO")
	private String intermediaryPhNo;

	//TPA Details
	@Column(name = "TPA_NAME")
	private String tpaName;

	@Column(name = "TPA_CONTACTNAME")
	private String tpaContactName;

	@Column(name = "TPA_EMAIL")
	private String tpaEmail;

	@Column(name = "TPA_PHNO")
	private String tpaPhNo;
	
	@Column(name = "APPLICATION_STATUS")
	private String appStatus;

	@Column(name = "CREATED_DATE")
	private String createDate;

	@Column(name = "UPDATED_DATE")
	private String updateDate;

	@Column(name = "RECORD_STATUS")
	private String recordStatus;
	
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "locationId")
	private Location location;
	
	@PrePersist
    private void generateCustomSequence() {
		LocalDateTime currentTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
        String formattedTimestamp = currentTime.format(formatter);
        String customSequence = prodCategoryId+policyType+productId+formattedTimestamp;
        this.rfqId = customSequence;
    }
}
