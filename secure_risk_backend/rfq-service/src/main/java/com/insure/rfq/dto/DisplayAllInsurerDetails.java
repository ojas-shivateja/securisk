package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayAllInsurerDetails {
    private Long  insurerId;

    private String bankName;

    private String branch;

    private String location;

    private String ifscCode;

    private Long accountNumber;
    private String accountHolderNumber;
}
