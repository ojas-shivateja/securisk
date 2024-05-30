package com.insure.rfq.login.dto;

import com.insure.rfq.login.entity.Department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class DesignationLoginDto {
	private Long id;
	private String designationName;
	private String status;
	private Department department;
}