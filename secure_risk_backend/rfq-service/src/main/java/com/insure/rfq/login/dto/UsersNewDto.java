package com.insure.rfq.login.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.entity.Location;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersNewDto {

	@JsonIgnore
    private Long userId;
    
    @NotBlank(message = "Corporate name cannot be blank")
    private String corporateName;
    
    @NotBlank(message = "Business type cannot be blank")
    private String businessType;
    
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    
    @NotBlank(message = "Last name cannot be blank")
    private String employeeId;
    
    @JsonIgnore
    private Department department;
    
    @JsonIgnore
    private Designation designation;
    
    @JsonIgnore
    private Location location;
    
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth cannot be null")
    private LocalDate dateOfBirth;
    
    @Positive(message = "Age must be a positive value")
    @NotNull(message = "Age cannot be null")
    private int age;
    
    @NotBlank(message = "Gender cannot be blank")
    private String gender;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number should contain only numbers")
    @Size(min = 10, max = 10, message = "Phone number should have exactly 10 digits")
    private String phoneNo;
    
    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!^&*()])[A-Za-z\\d@#$%!^&*()]{5,11}$", message = "Password must be at least 5 characters and atmost 11 characters , should contains at least one uppercase letter, one number, and one special character.")
    private String password;
    
    @JsonIgnore
    private String status;

}
