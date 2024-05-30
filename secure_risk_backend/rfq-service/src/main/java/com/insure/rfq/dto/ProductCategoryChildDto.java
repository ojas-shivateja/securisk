package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductCategoryChildDto {
	@JsonProperty(value = "categoryName")
	private String categoryName;
	@JsonProperty(value = "categoryId")
	private Long categoryId;
}
