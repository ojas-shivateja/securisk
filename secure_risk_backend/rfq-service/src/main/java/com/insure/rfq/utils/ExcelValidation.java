package com.insure.rfq.utils;

public enum ExcelValidation {
	
	BLANK("Blank or Empty"),
	NONE("No value"),
	NULL("Null value"),
	DATE("dd-MM-yyyy"),
	UNIVERSAL_DATE_FORMAT("dd-MM-yyyy"),
	BOOLEAN("Boolean value"),
	ERROR("Formula Error"),
	OTHER("In-valid value");
	
	public final String label;
	ExcelValidation(String label){
		this.label = label;
	}
	
	public String getValue() {
		return label;
	}
}
