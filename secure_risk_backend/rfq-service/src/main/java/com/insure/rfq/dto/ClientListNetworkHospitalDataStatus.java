package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListNetworkHospitalDataStatus {

    private boolean hospitalNameStatus;
    private String hospitalName;
    private String hospitalNameErrorMessage;

    private boolean addressStatus;
    private String address;
    private String addressErrorMessage;

    private boolean cityStatus;
    private String city;
    private String cityErrorMessage;

    private boolean stateStatus;
    private String state;
    private String stateErrorMessage;

    private boolean pinCodeStatus;
    private Long pinCode;
    private String pinCodeErrorMessage;

}
