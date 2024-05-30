package com.insure.rfq.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListEnrollementUploadDto {


    private String employeeId;

    private String employeeName;

    private String dateOfBirth;

    private String gender;

    private String relation;

    private String dateOfJoining;

    private String eCardNumber;

    private String policyStartDate;

    private String policyEndDate;

    private String baseSumInsured;

    private String topUpSumInsured;

    private String groupName;

    private String insuredCompanyName;
}
