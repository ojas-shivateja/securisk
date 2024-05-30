package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayAllEndorsementDto {
    private Long endorsementId;
    private String endorsementName;
    private String FileName;
}
