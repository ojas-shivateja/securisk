package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cd_balanceHeaderStatusDto {

    private boolean policyNumber;
    private boolean transactionType;
    private boolean paymentDate;
    private boolean amount;
    private boolean CR_DB_CD;
    private boolean Balance;
}
