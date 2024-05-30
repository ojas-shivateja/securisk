package com.insure.rfq.login.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DesignationOperationMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long designationId;
	private Long operationId;
	
	@Temporal(TemporalType.DATE)
	private LocalDate createDate;
	
	@Temporal(TemporalType.DATE)
	private LocalDate updatedDate;
	

}
