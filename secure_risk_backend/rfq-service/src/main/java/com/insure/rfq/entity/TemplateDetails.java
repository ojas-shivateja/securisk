package com.insure.rfq.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "TEMPLATE_DETAILS")
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotEmpty
	@Column(name = "TEMPLATE NAME")
	private String templateName;
	@NotEmpty
	@Column(name = "TEMPLATE TYPE")
	private String templateType;
	@NotEmpty
	@Column(name = "TYPE")
	private String type;

	@Column(name = "TEMPLATE FILE")
	private byte[] templateFile;
	@Column(name = "TEMPLATE_FILE_NAME")
	private String templateFileName;

	@Column(name = "PERMISSION")
	private String permissions;
	@ManyToOne
	@JoinColumn(name = "PRODUCT_ID")
	private Product product;
	@Column(name = "CREATEDATE")
	private Date createDate; // Added createdate field

	@Column(name = "UPDATEDATE")
	private Date updateDate; // Added updatedate field

}
