package com.insure.rfq.repository;

import com.insure.rfq.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p.productId as productId,p.productName as productName FROM Product p WHERE p.productcategory.categoryId = :categoryId and status='ACTIVE'")
	List<Map<String, Object>> findProductNamesByCategory(@Param("categoryId") Long categoryId);

	Optional<Product> findByProductId(Long prodId);

	Product findByProductName(String productName);


	@Query("SELECT product FROM Product product JOIN product.clientList clientList WHERE clientList.cid = :clientListId")
	List<Product> findByClientList(@Param("clientListId") Long clientListId);

	@Query(value = "SELECT COUNT(*) FROM PRODUCT;", nativeQuery = true)
	Long countApplicationsByStatus();
	
	@Query("SELECT pc.productcategory.categoryName FROM Product pc WHERE pc.productId = :productid")
	String findCategoryNameByProductId(@Param("productid") Long productid);

}