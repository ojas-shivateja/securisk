package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ProductChildDto {
	@JsonProperty(value = "productId")
	private Long productId;
	@JsonProperty(value = "productName")
	private String productName;
}
