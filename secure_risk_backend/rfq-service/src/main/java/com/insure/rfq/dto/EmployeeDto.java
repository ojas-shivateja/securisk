package com.insure.rfq.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
	
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
