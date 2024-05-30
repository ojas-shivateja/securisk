package com.insure.rfq.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDepedentDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "RFQID")
	private String rfqId;

	@Column(name = "EMPLOYEEID")
	private String employeeId;

	@Column(name = "EMPLOYEENAME")
	private String employeeName;

	@Column(name = "RELATIONSHIP")
	private String relationship;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "AGE")
	private String age;

	@Column(name = "DATEOFBIRTH")
	private Date dateOfBirth;

	@Column(name = "SUMINSURED")
	private double sumInsured;

	@Column(name = "CREATEDDATE")
	private Date createdDate;

	@Column(name = "UPDATEDDATE")
	private Date updatedDate;

	@Column(name = "RECORDSTATUS")
	private String recordStatus;
	
}
