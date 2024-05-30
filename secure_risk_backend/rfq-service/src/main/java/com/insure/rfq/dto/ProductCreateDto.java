package com.insure.rfq.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {

	@NotEmpty
	@NotNull
	private String productName;
	@NotEmpty
	@NotNull
	private String productcategoryId;
}
