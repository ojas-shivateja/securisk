package com.insure.rfq.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.insure.rfq.login.entity.Designation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientListUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long uid;

	@Column(name = "employeeId")
	private String employeeId;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Designation designationId;

	@Column(name = "mailId")
	private String mailId;

	@Column(name = "phoneNo")
	private String phoneNo;

	private String status;

	@ManyToOne
	@JoinColumn(referencedColumnName = "cid")
	@JsonBackReference
	private ClientList clientList;

	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;

}
