package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllClientListNetWorkHospitalDto {

    private String hospitalName;
    private String address;
    private String city;
    private String state;
    private Long pinCode;

}
