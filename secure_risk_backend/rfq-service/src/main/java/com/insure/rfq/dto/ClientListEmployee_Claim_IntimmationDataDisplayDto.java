package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListEmployee_Claim_IntimmationDataDisplayDto {

    private Long clientListEmployee_Claim_Intimmation;
    private String patient_Name;
    private String relationToEmployee;
    private String employeeName;
    private String emailId;
    private String hospital;
    private String doctorName;
    private String uhid;
    private String employeeId;
    private String mobileNumber;
    private String reasonForAdmission;
    private String dateOfHospitalisation;
    private String other_Details;
    private String rfqId;

    private String productId;

    private String clientListId;
    private String employeeDataID;

    private  String  sumInsured;
    private String reasonforClaim;
}
