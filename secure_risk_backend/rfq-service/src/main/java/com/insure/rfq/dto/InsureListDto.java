package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsureListDto {

//	@NotNull(message = "cannot be null ")
	private String insurerName;
//	@NotNull(message = "cannot be null ")
	private String location;

	private String email;

	private String managerName;

	private String phoneNumber;

}
