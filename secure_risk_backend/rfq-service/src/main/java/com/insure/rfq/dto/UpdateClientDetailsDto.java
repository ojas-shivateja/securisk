package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientDetailsDto {
@JsonProperty(value="ClientDetailsName")

    private String clientDetailsName;

@JsonProperty(value = "fileName")
    private MultipartFile fileName;

}
