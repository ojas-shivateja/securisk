package com.insure.rfq.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsurerUsersDto {

	private String userId;
	@Email
	private String email;
	@NotNull(message = "cannot be null ")
	private String managerName;

	private Long phoneNumber;
	@NotNull(message = "cannot be null ")
	private String location;

}
