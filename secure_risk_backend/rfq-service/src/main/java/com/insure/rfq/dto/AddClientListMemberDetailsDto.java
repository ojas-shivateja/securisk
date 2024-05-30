package com.insure.rfq.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddClientListMemberDetailsDto {


    @JsonProperty(value="employeeNo")
    private String employeeNo;

    @JsonProperty(value="name")
    private String name;

    @JsonProperty(value="relationShip")
    private String relationShip;

    @JsonProperty(value="gender")
    private String gender;

    @JsonProperty(value="dateOfBirth")
    private String dateOfBirth;

    @JsonProperty(value="age")
    private Double age;

    @JsonProperty(value="sumInsured")
    private Double sumInsured;

    @Email
    @JsonProperty(value="email")
    private String email;

    @Pattern(regexp="(^$|[0-9]{10})")
    @JsonProperty(value="phoneNumber")
    private String phoneNumber;

    @JsonProperty(value="designationId")
    private String designationId;

    @JsonProperty(value="departmentId")
    private String departmentId;

    @JsonProperty(value="role")
    private String role;

}
