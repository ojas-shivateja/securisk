package com.insure.rfq.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddClientListUserDto {

	private String employeeId;

	private String name;

	private String designationId;

	@Email
	private String mailId;

	private String phoneNo;

}
