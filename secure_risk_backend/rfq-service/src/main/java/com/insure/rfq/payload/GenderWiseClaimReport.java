package com.insure.rfq.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderWiseClaimReport {
	private String gender;
	private int genderCount;
	private double countPerct;
	private double amount;
	private double amountPerct;
}
