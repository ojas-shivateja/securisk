package com.insure.rfq.login.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersNewDtoGet {

	
    private Long userId;
    
    private String corporateName;
    
    private String businessType;
    
    private String firstName;
    
    private String lastName;
    
    private String employeeId;
    
    private String department;
    
    private String designation;
    
    private String location;
    
    private LocalDate dateOfBirth;
    
    private int age;
    
    private String gender;
    
    private String email;
    
    private String phoneNo;

    
    @JsonIgnore
    private String status;


}
