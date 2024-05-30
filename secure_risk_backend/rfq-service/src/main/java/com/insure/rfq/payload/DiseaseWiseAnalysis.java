package com.insure.rfq.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiseaseWiseAnalysis {
private int sl_No;
private String diseaseName;
private int diseaseCount;
private double amount;

}
