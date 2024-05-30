package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayAllClientDetailsDto {
    private Long clientDetailsId;
    private String clientDetailsName;
    private String fileName;
}
