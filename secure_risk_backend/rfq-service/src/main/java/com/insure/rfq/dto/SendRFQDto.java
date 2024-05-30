package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SendRFQDto {
	
	@JsonProperty(value = "rfqId")
	private String rfqId;
	
	@JsonProperty(value = "recordStatus")
	private String recordStatus;
}
