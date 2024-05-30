package com.insure.rfq.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.insure.rfq.entity.ClientList;

import lombok.Data;

@Data
public class ProductDto {

	private Long productId;

//	@NotBlank
	@JsonProperty(value = "productName")
	private String productName;

//	@NotBlank
	@JsonProperty(value = "productCategory")
	private ProductCategoryDto productcategory;

	@JsonProperty(value = "insureCompany")
	private String insureCompany;

	@JsonProperty(value = "activeStatus")
	private String activeStatus;

	@JsonProperty(value = "policyType")
	private String policyType;

	@JsonProperty(value = "tpaList")
	private String tpaList;

	@JsonIgnore
	@JsonProperty(value = "clientList")
	private ClientList clientList;

	@JsonProperty(value = "coverages")
	private int coverages;

	@JsonProperty(value = "createDate")
	private Date createDate;

	@JsonProperty(value = "updateDate")
	private Date updateDate;

}
