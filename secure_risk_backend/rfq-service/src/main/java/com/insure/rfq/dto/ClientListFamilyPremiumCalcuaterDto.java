package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClientListFamilyPremiumCalcuaterDto {

	
	private Integer ageBandStart;
	private Integer ageBandEnd;
	private Double sumInsured;
	private Integer basePremium;

	// _________________ ID's
	private Long familypremiumCalcuater_id;
	private String rfqId;


	private String productId;


	private String clientListId;

}
