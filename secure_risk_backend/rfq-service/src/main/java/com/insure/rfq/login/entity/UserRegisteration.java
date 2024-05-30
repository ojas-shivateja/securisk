package com.insure.rfq.login.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserRegisteration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String corporateName;
	private String businessType;
	private String firstName;
	private String lastName;
	private String employeeId;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	@JsonIgnore
	private Department department;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	@JsonIgnore
	private Designation designation;
	@ManyToOne
	@JoinColumn(referencedColumnName = "locationId")
	@JsonIgnore
	private Location location;
	private LocalDate dateOfBirth;
	private int age;
	private String gender;
	 @Column(unique = true)
	private String email;
	private String phoneNo;
	private String password;
	private String status;
	private String otp;
	
}
