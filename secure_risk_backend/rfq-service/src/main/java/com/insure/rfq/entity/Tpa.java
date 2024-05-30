package com.insure.rfq.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TPA")
public class Tpa {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tpa_id")
	private Long tpaId;

	@Column(name = "tpaName")
	private String tpaName;

	@Column(name = "location")
	private String location;
	
	@Column(name = "CREATEDDATE")
	private String createdDate;
	
	@Column(name = "UPDATEDDATE")
	private String updatedDate;
	
	@Column(name = "RECORDSTATUS")
	private String recordStatus;
	
	@OneToMany(mappedBy = "tpaList", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonManagedReference
	private List<ClaimsTPAHeaders> tpaHeaders;
}
