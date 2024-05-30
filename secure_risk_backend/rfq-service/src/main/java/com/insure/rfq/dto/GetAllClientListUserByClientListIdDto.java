package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllClientListUserByClientListIdDto {

	private String employeeId;
	private String name;
	private String emailId;
	private String phoneNumber;
}
