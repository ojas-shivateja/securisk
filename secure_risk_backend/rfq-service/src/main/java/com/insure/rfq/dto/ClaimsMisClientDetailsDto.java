package com.insure.rfq.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimsMisClientDetailsDto {




    @JsonProperty(value ="claimsMiscFilePath")
    MultipartFile claimsMiscFilePath;

}
