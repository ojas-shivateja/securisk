package com.insure.rfq.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListContactDetailsDto {

    private Long contactId;

    private String employeeId;

    private String name;

    @Email
    private String email;

    @Pattern(regexp="(^$|[0-9]{10})")
    private String phoneNumber;

    private String designation;

    private String role;

}
