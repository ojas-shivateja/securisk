package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCorporateDetailsDto {

    @JsonProperty(value = "productId")
    private String productId;

    @JsonProperty(value ="productCategoryId" )
    private String productCategoryId;

    @JsonProperty(value ="policyType" )
    private String policyType;

    //Corporate Details
    @JsonProperty(value = "insuredName")
    private String insuredName;


    @JsonProperty(value = "address")
    private String address;


    @JsonProperty(value = "nob")
    private String nob;


    @JsonProperty(value = "nobCustom")
    private String nobCustom;

    @JsonProperty(value = "contactName")
    private String contactName;


    @Email
    @JsonProperty(value = "email")
    private String email;


    @JsonProperty(value = "phNo")
    @Pattern(regexp="(^$|[0-9]{10})")
    private String phNo;

    //Intermediary Details

    @JsonProperty(value = "intermediaryName")
    private String intermediaryName;


    @JsonProperty(value = "intermediaryContactName")
    private String intermediaryContactName;


    @Email
    @JsonProperty(value = "intermediaryEmail")
    private String intermediaryEmail;


    @Size(min = 10, max = 10)
    @JsonProperty(value = "intermediaryPhNo")
    @Pattern(regexp="(^$|[0-9]{10})")
    private String intermediaryPhNo;

    //TPA Details
    @JsonProperty(value = "tpaName")
    private String tpaName;


    @JsonProperty(value = "tpaContactName")
    private String tpaContactName;


    @Email
    @JsonProperty(value = "tpaEmail")
    private String tpaEmail;


    @Size(min = 10, max = 10)
    @Pattern(regexp="(^$|[0-9]{10})")
    @JsonProperty(value = "tpaPhNo")
    private String tpaPhNo;


    @JsonProperty(value = "location")
    private String location;
}
