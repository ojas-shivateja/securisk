package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayAllMyDetailsDto {

    public Long mydetailId;
    public String detailName;
    public String fileName;
}
