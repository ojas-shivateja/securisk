package com.insure.rfq.login.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Designation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String designationName;
	@OneToMany(mappedBy = "designation")
	@JsonIgnore
	private List<UserRegisteration> usersNew;
	private String status;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	@JsonIgnore
	private Department department;

}
