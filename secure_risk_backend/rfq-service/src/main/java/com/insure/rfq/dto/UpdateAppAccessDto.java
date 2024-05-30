package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAppAccessDto {
    private String employeeNo;

    private String name;

    private String relationship;

    private String age;

    private String role;

    private String dateOfBirth;

    private String  email;

    private String phoneNumber;

    private String designation;

    private String department;

    private String gender;

    private String sumInsured;
}
