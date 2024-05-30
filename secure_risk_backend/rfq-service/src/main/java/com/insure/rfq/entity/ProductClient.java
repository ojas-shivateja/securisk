package com.insure.rfq.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductClient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
	private String productType;
	private String policyType;
	private String tpaList;
	private String insurerCompany;
	private int status;
}
