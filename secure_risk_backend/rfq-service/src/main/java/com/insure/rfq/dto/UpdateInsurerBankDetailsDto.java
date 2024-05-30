package com.insure.rfq.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInsurerBankDetailsDto {

    @JsonProperty(value="bankName")
    private String bankName;
    @JsonProperty(value="branch")
    private String branch;
    @JsonProperty(value="location")
    private String location;
    @JsonProperty(value="ifscCode")
    private String ifscCode;
    @JsonProperty(value="accountNumber")
    private long accountNumber;
    @JsonProperty(value="accountHolderNumber")
    private String accountHolderNumber;
}
