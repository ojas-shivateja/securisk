package com.insure.rfq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AccountManagerSumInsured")
public class   AccountManagerSumInsured {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "sumInsured_id")
	private Long sumInsuredId;

	@Column(name = "sumInsured_name")
	private String sumInsuredName;

	@Column(name = "sumInsured_File")
	private String sumInsuredFileName;

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

	@Column(name = "RECORD_STATUS")
	private String recordStatus;
}
