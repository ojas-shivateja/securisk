package com.insure.rfq.login.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationMapped {

private String operationType;
private Boolean isOperationMapped;
private List<SubOperation> subOperation;

}
