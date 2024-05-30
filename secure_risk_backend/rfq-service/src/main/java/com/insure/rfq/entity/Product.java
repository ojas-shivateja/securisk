package com.insure.rfq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "PRODUCT")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_sequence_generator")
	@SequenceGenerator(name = "product_sequence_generator", sequenceName = "product_id_sequence", allocationSize = 50, initialValue = 1000)
	@Column(name = "PRODUCTID")
	private Long productId;

	@Column(name = "PRODUCTNAME", length = 100)
	private String productName;

	@ManyToOne
	@JoinColumn(name = "CATEGORYID")
	@JsonIgnore
	private ProductCategory productcategory;

	@ManyToOne
	@JoinColumn(referencedColumnName = "insurerId")
	private InsureList insureCompanyId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "POLICYTYPE")
	private String policyType;

	@ManyToOne
	@JoinColumn(referencedColumnName = "tpa_id")
	private Tpa tpaId;

	@Column(name = "COVERAGES")
	private int coverages;

	@Column(name = "CREATEDDATE")
	private Date createdDate;

	@Column(name = "UPDATEDDATE")
	private Date updatedDate;

	@ManyToMany()
	@JoinTable(name = "PRODUCT_CLIENTLIST", joinColumns = { @JoinColumn(name = "PRODUCTID") }, inverseJoinColumns = {
			@JoinColumn(name = "CID") })
	@JsonManagedReference
	private List<ClientList> clientList;

}