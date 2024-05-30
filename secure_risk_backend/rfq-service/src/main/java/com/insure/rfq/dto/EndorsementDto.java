package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EndorsementDto {



    @JsonProperty(value = "endorsementId")
    private long endorsementId;

    private String endorsementName;

    @JsonProperty(value = "fileName")
    @JsonIgnore
    private MultipartFile fileName;
    @JsonIgnore
    private String rfqId;
    
   @JsonIgnore
    private byte[] fileData;




}
