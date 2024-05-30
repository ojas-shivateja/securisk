package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cd_balanceHeaderMappingDto {
    private  Long sheetId;
    private List<Cd_balanceDetailsHeadersDto> headers;


}
