package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimsDumpDto {

    private double claimPaidReimbursement;

    private double claimsPaidCashless;

    private double claimsOutStandingReimbursement;

    private double claimsOutStandingCashless;

}
