package com.insure.rfq.dto;

import lombok.Data;

@Data
public class EmpDepdentValidationDto {
	private boolean sno;
	private boolean employeeId;
	private boolean employeeName;
	private boolean relationship;
	private boolean gender;
	private boolean age;
	private boolean dateOfBirth;
	private boolean sumInsured;
}
