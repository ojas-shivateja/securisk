package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListMemberDetailsDataStatus {


    private boolean employeeIdStatus;
    private String employeeId;
    private String employeeIdErrorMessage;

    private boolean employeeNameStatus;
    private String employeeName;
    private String employeeNameErrorMessage;

    private boolean dateOfBirthStatus;
    private String dateOfBirth;
    private String dateOfBirthErrorMessage;


    private boolean dateOfJoiningStatus;
    private String dateOfJoining;
    private String dateOfJoiningErrorMessage;

    private boolean eCardNumberStatus;
    private String eCardNumber;
    private String eCardNumberErrorMessage;

    private boolean relationshipStatus;
    private String relationship;
    private String relationshipErrorMessage;

    private boolean genderStatus;
    private String gender;
    private String genderErrorMessage;
    
    
    private boolean ageStatus;
    private String age;
    private String ageErrorMessage;


    private boolean policyStartDateStatus;
    private String policyStartDate;
    private String policyStartDateErrorMessage;


    private boolean policyEndDateStatus;
    private String policyEndDate;
    private String policyEndDateErrorMessage;


    private boolean sumInsuredStatus;
    private String sumInsured;
    private String sumInsuredErrorMessage;


    private boolean groupNameStatus;
    private String groupName;
    private String groupNameErrorMessage;


    private boolean insuredCompanyNameStatus;
    private String insuredCompanyName;
    private String insuredCompanyNameErrorMessage;

    private String remarks;
}
