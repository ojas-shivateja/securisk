package com.insure.rfq.login.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignationOperationMappingHistory {
	@Id
	private Long id;
	private Long designationId;
	private Long operationId;
	private LocalDate createDate;
	private LocalDate updatedDate;
	private String remark;
	

}
