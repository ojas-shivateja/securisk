package com.insure.rfq.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CliamTypeAnalysis {
	private int cashlessCalimCount;
	private int reimbursementCliamCount;
	private double cashlessCalimCountAmount;
	private double reimbursementCliamCountAmount;
}
