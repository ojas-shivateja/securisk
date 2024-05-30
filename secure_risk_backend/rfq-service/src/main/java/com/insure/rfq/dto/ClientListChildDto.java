package com.insure.rfq.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListChildDto {

	@NotEmpty
	@NotNull
	private String clientName;

	@NotEmpty
	@NotNull
	private String location;
}
