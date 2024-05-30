package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cd_balanceValueStatus {
    private String policyNumber;
    private boolean policyNumberStatus;
    private String policyNumberErrorMessage;


    private String transactionType;
    private String transactionTypeErrorMessage;
    private boolean transactionTypeStatus;

    private String paymentDate;
    private String paymentDateErrorMessage;
    private boolean paymentDateStatus;

    private Double amount;
    private String amountErrorMessage;
    private boolean amountStatus;

    private String CR_DB_CD;
    private String CR_DB_CDErrorMessage;
    private boolean CR_DB_CDStatus;

    private Double balance;
    private String balanceErrorMessage;
    private boolean balanceStatus;
}
