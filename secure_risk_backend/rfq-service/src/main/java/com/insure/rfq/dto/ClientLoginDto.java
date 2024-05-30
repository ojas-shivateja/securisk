package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientLoginDto {

    private String accessToken;

    private String employeeId;

    private Long productId;

    private Long clientListId;



}
