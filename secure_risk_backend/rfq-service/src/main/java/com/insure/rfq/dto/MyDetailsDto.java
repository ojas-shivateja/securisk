package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MyDetailsDto {

    @JsonProperty(value = "mydetailsId")
    private long mydetailId;

    @JsonProperty(value = "detailName")
    private String detailName;

    @JsonProperty(value = "fileName")
    private MultipartFile fileName;

    @JsonIgnore
    private String rfqId;

    @JsonIgnore
    private byte[] fileData;

}
