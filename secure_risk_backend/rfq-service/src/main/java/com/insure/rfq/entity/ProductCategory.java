package com.insure.rfq.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "PRODUCT_CATEGORY")
public class ProductCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_category_sequence_generator")
	@SequenceGenerator(name = "product_category_sequence_generator", sequenceName = "product_category_sequence", allocationSize = 1, initialValue = 100)
	@Column(name = "CATEGORYID")
	private Long categoryId;

	@Column(name = "CATEGORYNAME", length = 100)
	private String categoryName;

	@Column(name = "CREATEDDATE")
	private Date createdDate;

	@Column(name = "UPDATEDDATE")
	private Date updatedDate;

	@Column(name = "ACTIVESTATUS")
	private String activeStatus;
	
	@OneToMany(mappedBy = "productcategory", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<Product> products = new ArrayList<>();
}
