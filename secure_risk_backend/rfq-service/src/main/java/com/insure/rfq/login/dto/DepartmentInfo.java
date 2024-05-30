package com.insure.rfq.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentInfo {
private Long departmentId;
private String departmentName;
private Boolean isDepartmentInfoPermitted;
}
