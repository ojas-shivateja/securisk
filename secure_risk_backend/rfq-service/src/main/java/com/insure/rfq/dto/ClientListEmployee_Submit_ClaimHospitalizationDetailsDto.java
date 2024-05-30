package com.insure.rfq.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListEmployee_Submit_ClaimHospitalizationDetailsDto {
    private String user_detailsId;
    private String State;
    private String city;
    private String hospitalName;
    private String hospitalAddress;
    private String natureofIllness;
    private String preHospitalizationAmount;
    private String postHospitalizationAmount;
    private String totalAmountClaimed;
    private String hospitalizationAmount;
//Medical Expenses Details

    private List<String> medicalExpenses_BillNo;

    private List<Double> medicalExpensesBillAmount;
    private List<String> medicalExpenses_BillDate;

    private List<String> medicalExpensesRemarks;

}
