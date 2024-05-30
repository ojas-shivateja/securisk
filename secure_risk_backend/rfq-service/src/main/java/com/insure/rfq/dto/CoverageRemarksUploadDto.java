package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverageRemarksUploadDto {

    private String employeeIdValue;
    private String remarks;
    private String relationshipValue;
    private String employeeNameValue;
    private String genderValue;
    private String sumInsuredValue;
    private String dateOfBirthValue;
    private String ageValue;
}
