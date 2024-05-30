package com.insure.rfq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientProductAssociation;

public interface ClientListProductAssociationRepository extends JpaRepository<ClientProductAssociation, Long> {

	List<ClientProductAssociation> findByClientList(ClientList clientList);

	@Query("SELECT c FROM ClientProductAssociation c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId")
	Optional<ClientProductAssociation> findByClientListAndProduct(@Param("clientListId") Long clientListId,
			@Param("productId") Long productId);

}
