package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cd_balanceDisplayDto {
    private String policyNumber;
    private String transactionType;
    private String paymentDate;
    private Double amount;
    private String CR_DB_CD;
    private Double Balance;
    private Long cd_balanceId;
}
