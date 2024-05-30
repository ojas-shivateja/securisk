package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ClientDetailsDto{

    @JsonProperty(value = "clientDetailsId")
   private Long clientDetailsId;


    @JsonProperty(value = "ClientDetailsName")

    private String clientDetailsName;

    @JsonProperty(value = "filePath")
    @JsonIgnore
    private MultipartFile fileName;

    @JsonIgnore
    private String rfqId;

//    private String clientListId;
//
//    private String productId;


}
