package com.insure.rfq.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoverageUploadDto {
	@NotBlank
	@JsonProperty(value = "rfqId")
	private String rfqId;
	@JsonProperty(value = "fileType")
	private String fileType;
	@JsonProperty(value = "tpaName")
	private String tpaName;
	@JsonProperty(value = "file")
	private MultipartFile file;
	@JsonProperty(value = "fileName")
	private String fileName;
}
