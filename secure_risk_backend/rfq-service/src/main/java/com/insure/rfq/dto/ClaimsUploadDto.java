package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimsUploadDto {

	private String policyNumber;
	
	private String startDate;
	
	private String endDate;
}
