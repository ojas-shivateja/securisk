package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ClientListEmployee_Claim_IntimmationDto {


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

    private  String  sumInsured;
    private String reasonforClaim;



}
