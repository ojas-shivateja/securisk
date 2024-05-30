package com.insure.rfq.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountManagerSumInsuredDto {

	private String sumInsuredName;
	private MultipartFile sumInsuredFileName;
}
