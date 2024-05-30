package com.insure.rfq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SendRFQ_Entity")
@Data
public class SendRFQEntity {
	
	@Id
	private Long id;
	
	@Column(name = "RFQ_ID")
	private String rfqId;
	
	@Column(name = "RECORD_STATUS")
	private String recordStatus;

}
