package com.insure.rfq.login.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long locationId;

	@Column(name = "sno")
	private String sno;
	@Column(name = "location", unique = true)
	private String locationName;
	@OneToMany(mappedBy = "location")
	@JsonManagedReference
	private List<UserRegisteration> usersNew;
	private String status;

}
