package com.insure.rfq.login.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignationBasedOperation {
private Long desginationId;
private String designationName;
private Set<OperationMapped> operationMapped;
private Boolean isDesignationBasedOperationPermitted;
}
