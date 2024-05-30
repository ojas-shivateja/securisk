package com.insure.rfq.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInsurerUserDto {

	@Email
	private String email;
	private String managerName;

	private Long phoneNumber;
	private String location;

}
