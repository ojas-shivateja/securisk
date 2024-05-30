package com.insure.rfq.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

	@JsonProperty(value = "to")
    private List<String> to;
	@JsonProperty(value = "filePath")
    private List<String> filePath;
	private List<EmailFileDTo>getAllFiles;

	private String rfqId;
}

