package com.insure.rfq.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;


@Entity
@Table(name = "EMP_DEPENDENT_HEADERS")
@Data
public class EmpDependentHeaders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HEADERID")
	private Long headerId;
	
	@Column(name = "HEADERNAME")
	private String headerName;
	@Column(name = "HEADECATEGORY")
	private String headerCategory;
	@Column(name = "CREATEDDATE")
	private String createdDate;
	@Column(name = "UPDATEDDATE")
	private String updatedDate;
	@Column(name = "STATUS")
	private String status;
	
	@OneToMany(mappedBy = "reportHeaders", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@ToString.Exclude
	private List<EmpDependentHeaderMapping> headers = new ArrayList<>();
	
}
