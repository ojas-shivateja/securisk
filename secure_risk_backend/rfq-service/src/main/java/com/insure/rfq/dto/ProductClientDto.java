package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductClientDto {
	private Long pid;
	private String productType;
	private String policyType;
	private String tpaList;
	private String insurerCompany;
	@JsonIgnore
	private int status;
}
