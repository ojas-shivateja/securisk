package com.insure.rfq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.ClientList;

@Repository
public interface ClientListRepository extends JpaRepository<ClientList, Long> {


	@Query(value = "SELECT status, COUNT(*) FROM client_list GROUP BY status", nativeQuery = true)
	List<Object[]> countApplicationsByStatus();

	Optional<ClientList> findByRfqId(String rfqId);
	
	@Modifying
	@Query(value = "UPDATE product_clientlist SET productid = :newproductid WHERE cid = :cid AND productid = :oldproductid", nativeQuery = true)
	void updateProductRelationship(@Param("newproductid") Long newProductId, @Param("cid") Long clientListId,
								   @Param("oldproductid") Long oldProductId);
	@Modifying
	@Query(value = "DELETE FROM product_clientlist WHERE  productid =:productid AND cid =:cid", nativeQuery = true)
	void deleteProductRelationShip(@Param("productid") Long productid, @Param("cid") Long cid);

}