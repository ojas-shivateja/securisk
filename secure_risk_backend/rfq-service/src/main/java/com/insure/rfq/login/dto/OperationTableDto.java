package com.insure.rfq.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationTableDto {
	private Long id;
	private String menuType;
	private String menuName;
}
