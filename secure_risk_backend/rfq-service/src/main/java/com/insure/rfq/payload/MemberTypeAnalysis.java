package com.insure.rfq.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberTypeAnalysis {
	private int mainMemberCount;
	private int dependentCount;
	private double mainMemberCountAmount;	
	private double dependentCountDepedentAmount;

}
