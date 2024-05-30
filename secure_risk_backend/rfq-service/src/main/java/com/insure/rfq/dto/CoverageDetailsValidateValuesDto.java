package com.insure.rfq.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
public class CoverageDetailsValidateValuesDto {

    @JsonProperty(value = "employee_id")
    private String employeeId;

    @JsonProperty(value = "employee_name")
    private String employeeName;

    @JsonProperty(value = "relationship")
    private String relationship;

    @JsonProperty(value = "gender")
    private String gender;

    @JsonProperty(value = "age")
    private int age;

    @JsonProperty(value = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @JsonProperty(value = "sum_insured")
    private Double sumInsured;
    
}
