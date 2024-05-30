package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetClientListMemberDetailsActiveListCountDto {

	private Long memberId;

	private String employeeNo;

	private String name;

	private String relationShip;

	private String dateOfBirth;

	private String gender;

	private String month;

	private Double age;

	private Double sumInsured;

	private String email;

	private String phoneNumber;

	private String designation;

	private String department;

	private String role;

	private Long employeeCount;

	private Long dependentCount;

	private Long totalCount;
}
