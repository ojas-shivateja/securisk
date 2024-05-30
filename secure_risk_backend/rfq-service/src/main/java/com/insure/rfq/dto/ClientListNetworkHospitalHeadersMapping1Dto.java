package com.insure.rfq.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListNetworkHospitalHeadersMapping1Dto {

    private boolean hospitalNameStatus;

    private boolean addressStatus;

    private boolean cityStatus;

    private boolean stateStatus;

    private boolean pinCodeStatus;


}
