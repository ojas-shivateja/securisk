package com.insure.rfq.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

	
	private String password;
		
	private String newPassword;
	
	private String confirmPassword;
	
}
