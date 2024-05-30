package com.insure.rfq.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.insure.rfq.entity.Product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCategoryDto {

	private Long categoryId;

	@NotBlank
	@JsonProperty(value = "categoryName")
	private String categoryName;

	@JsonProperty(value = "createDate")
	private Date createDate;

	@JsonProperty(value = "updateDate")
	private Date updateDate;

	@JsonProperty(value = "recordStatus")
	private boolean recordStatus;

	@JsonProperty(value = "products")
	private List<Product> products;
}
