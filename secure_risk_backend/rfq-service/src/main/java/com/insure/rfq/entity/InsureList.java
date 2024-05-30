package com.insure.rfq.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.insure.rfq.login.entity.Location;

import jakarta.persistence.Entity;
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
public class InsureList {
	@Id
	private String insurerId;
	private String insurerName;
	@ManyToOne
	@JoinColumn(referencedColumnName = "locationId")
	private Location locationId;
	private boolean status;
	@OneToMany(mappedBy = "insureList")
	@JsonManagedReference
	private List<InsureUsers> users = new ArrayList<>();

}
