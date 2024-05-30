package com.insure.rfq.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RfqQuoteDto {


    private String companyName;

    private String firstName;

    private String lastName;

    private String role;

    private String department;

    private String email;

    private String mobileNo;

    private String policyType;

    private int totalEmployees;

    private String status;

    private String date;

    private String familyDefinition;

    private String pinCode;

    private String city;

    private String state;

    private Boolean isCorporate;

    private Boolean isRetail;
}
