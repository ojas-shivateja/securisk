package com.insure.rfq.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgeWiseClaimAnalysis {
	private int ageCount0To10;
	private int ageCount11To20;
	private int ageCount21To30;
	private int ageCount31To40;
	private int ageCount41To50;
	private int ageCount51To60;
	private int ageCount61To70;
	private int ageCount70AndAbove;


	private double ageCount0To10Amount;
	private double ageCount11To20Amount;
	private double ageCount21To30Amount;
	private double ageCount31To40Amount;
	private double ageCount41To50Amount;
	private double ageCount51To60Amount;
	private double ageCount61To70Amount;
	private double ageCount70AndAboveAmount;

}
