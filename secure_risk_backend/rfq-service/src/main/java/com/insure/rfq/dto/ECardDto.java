package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ECardDto {

    private Long eCardId;

    @JsonIgnore
    private MultipartFile fileName;

    @JsonIgnore

    private String rfqId;
    
    @JsonIgnore
    private String employeeId;

    @JsonIgnore
    private byte[] fileData;
}
