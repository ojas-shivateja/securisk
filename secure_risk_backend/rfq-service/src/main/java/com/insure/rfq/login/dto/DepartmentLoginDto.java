package com.insure.rfq.login.dto;

import java.util.List;

import com.insure.rfq.login.entity.Designation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor	
public class DepartmentLoginDto {
	
	private Long id;
	private String departmentName;
	private String status;
	private List<Designation> designation;
}
