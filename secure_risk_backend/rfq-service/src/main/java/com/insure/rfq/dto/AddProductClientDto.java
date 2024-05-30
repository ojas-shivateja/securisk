package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddProductClientDto {

	private String productId;
	private String policyType;
	private String tpaId;
	private String insurerCompanyId;
}
