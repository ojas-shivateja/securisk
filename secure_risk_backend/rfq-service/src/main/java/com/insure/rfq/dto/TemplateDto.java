package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {
    @Id
    @GeneratedValue
    private Long id;
    @JsonFormat
    private String templateName;

    @JsonFormat
    private String templateType;

    @JsonFormat
    private String type;
    @Lob
    private MultipartFile templateFile;

    @JsonProperty(value = "templateFileName")
    private String templateFileName;

    @JsonFormat
    private String permissions;


    private Long productId;
    private Date createDate; // Added createdate field

    private Date updateDate; // Added updatedate field


}
