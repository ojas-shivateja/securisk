package com.insure.rfq.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insure.rfq.entity.ClientList;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListUserDto {

    private Long uid;

    private String employeeId;

    private String name;

    private String designation;

    @Email
    private String mailId;

    private String phoneNo;

    private String status;

    @JsonIgnore
    private ClientList clientList;

    private LocalDateTime createdDate;
    
    private LocalDateTime updatedDate;
}
