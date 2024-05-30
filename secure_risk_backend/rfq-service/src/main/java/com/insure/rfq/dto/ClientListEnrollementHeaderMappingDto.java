package com.insure.rfq.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListEnrollementHeaderMappingDto {

    private Long tpa_id;

    private List<EnrollementMemberDetailsHeadersDto> headers;

}
