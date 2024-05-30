package com.insure.rfq.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashBoardDto {
    private List<CooperateDetailsGraphDto> getCooperateDetailsGraphDtos;
    private long totalCount;
}
