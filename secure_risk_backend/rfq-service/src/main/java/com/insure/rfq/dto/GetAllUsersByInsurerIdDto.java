package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersByInsurerIdDto {

	private String userId;
	private String email;
	private String managerName;
	private Long phoneNumber;
	private String location;

}
