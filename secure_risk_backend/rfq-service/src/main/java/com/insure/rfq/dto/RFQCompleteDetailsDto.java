package com.insure.rfq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RFQCompleteDetailsDto {

    @JsonProperty(value = "corporateDetails")
    private CorporateDetailsDto corporateDetails;
    @JsonProperty(value = "coverageDetails")
    private CoverageDetailsDto coverageDetails;
    @JsonProperty(value = "policyTerms")
    private List<PolicyTermsDto> policyTerms;
}
