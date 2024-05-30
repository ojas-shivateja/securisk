package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ClientList_PreFamilyPremium_Calcuater")
public class ClientList_PreFamilyPremium_Calcuater {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long premiumCalcuater_id;

	@Column(name = "AGEBANDSTART")
	private Integer ageBandStart;

	@Column(name = "AGEBANDEND")
	private Integer ageBandEnd;
	
	@Column(name = "SUMINSURED")
	private Double sumInsured;
	
	@Column(name = "BASEPREMIUM")
	private Integer basePremium;
	
	@Column(name = "RECORDSTATUS")
	private String recordStatus;
	
	// _________________ ID's

	@Column(name = "RFQID")
	private String rfqId;

	@ManyToOne
	@JoinColumn(referencedColumnName = "productId")
	private Product productId;

	@ManyToOne
	@JoinColumn(referencedColumnName = "cid")
	private ClientList clientListId;

	@Column(name = "CREATEDDATE")
	private String createdDate;
	@Column(name = "UPDATEDDATE")
	private String updatedDate;

}
