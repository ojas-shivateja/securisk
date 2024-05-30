package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor@NoArgsConstructor
public class GetChildClientListHospitalizationDetailsDto {
    private String medicalExpenses_BillNo;
    private String medicalExpenses_BillDate;
    private Double medicalExpensesBillAmount;
    private String medicalExpensesRemarks;
}
