package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllClientListEnrollmentDto {

	private String employeeId;
	private String employeeName;
	private String dateOfBirth;
	private String gender;
	private String relation;
	private String dateOfJoining;
	private String eCardNumber;
	private String policyCommencementDate;
	private String policyValidUpto;
	private String baseSumInsured;
	private String topUpSumInsured;
	private String groupName;
	private String insuredCompanyName;
	@JsonIgnore
	private String age;
	private String month;

}
