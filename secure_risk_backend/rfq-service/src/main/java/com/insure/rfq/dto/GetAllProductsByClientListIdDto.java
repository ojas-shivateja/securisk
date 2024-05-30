package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAllProductsByClientListIdDto {

	private String productId;
	private String productCategoryId;
	private String productName;
	private String policyType;
	private String tpaId;
	private String insurerCompanyId;
}
