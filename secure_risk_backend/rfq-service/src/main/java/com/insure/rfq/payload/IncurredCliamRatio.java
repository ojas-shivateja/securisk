package com.insure.rfq.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IncurredCliamRatio {
	private String status;
	private int count;
	private double amount;
}
