package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllClientListPendingListDto {
    private Long memberId;
    private String employeeNo;
    private String name;
    private String relationShip;
    private String email;
    private String phoneNumber;
    private Double sumInsured;
    private String month;
    private String status;
    private String designation;
    private String department;
    private String role;
}
