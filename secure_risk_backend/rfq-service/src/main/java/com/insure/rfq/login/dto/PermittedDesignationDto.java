package com.insure.rfq.login.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermittedDesignationDto {
	
	private Long id;
	private List<OperationPermittedDto> operationPermittedDto;

}
