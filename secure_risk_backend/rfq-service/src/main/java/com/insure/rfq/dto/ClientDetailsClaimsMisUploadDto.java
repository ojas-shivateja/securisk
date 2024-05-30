package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ClientDetailsClaimsMisUploadDto {

    @JsonProperty(value = "fileType")
    private String fileType;
    @JsonProperty(value = "tpaName")
    private String tpaName;
    @JsonProperty(value = "file")
    private MultipartFile file;
    @JsonProperty(value = "fileName")
    private String fileName;
}
