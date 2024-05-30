package com.insure.rfq.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insure.rfq.entity.ClientList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListProductDto {

	private Long clLong;

	private String productType;

	private String insureCompany;

	private String status;

	private String policyType;

	private String tpaList;

	@JsonIgnore
	private ClientList clientList;

	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;
}
