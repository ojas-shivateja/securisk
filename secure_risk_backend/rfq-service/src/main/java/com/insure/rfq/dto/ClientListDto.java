package com.insure.rfq.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListDto {

	@NotNull
	@NotEmpty
	private String clientName;

	@NotNull
	@NotEmpty
	private String locationId;

	@NotNull
	@NotEmpty
	private String productId;

	@NotNull
	@NotEmpty
	private String insuranceCompanyId;

	private String policyType;

	private String tpaId;

//	private int sNo;
}
