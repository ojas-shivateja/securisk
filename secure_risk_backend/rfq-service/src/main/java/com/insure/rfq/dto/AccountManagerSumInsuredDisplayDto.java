package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class AccountManagerSumInsuredDisplayDto {
	private Long sumInsuredId;
	private String sumInsuredName;
	private String sumInsuredFileName;
}
