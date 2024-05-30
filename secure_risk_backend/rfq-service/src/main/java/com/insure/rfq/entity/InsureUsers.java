package com.insure.rfq.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.insure.rfq.login.entity.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsureUsers {
	@Id
	private String userId;
	@Column(unique = true)
	private String email;
	private String managerName;
	private Long phoneNumber;
	@ManyToOne
	@JoinColumn(referencedColumnName = "locationId")
	private Location location;
	private int status;
	@ManyToOne
	@JoinColumn(name = "insureList_Id")
	@JsonBackReference
	private InsureList insureList;
}
