package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.Tpa;

import jakarta.transaction.Transactional;

@Repository
public interface TpaRepository extends JpaRepository<Tpa, Long> {

	@Query("SELECT tpaName FROM Tpa WHERE recordStatus='ACTIVE'")
	List<String> getTpaList();

	@Transactional
	@Modifying
	@Query("UPDATE Tpa t set t.recordStatus='INACTIVE' WHERE t.tpaId=:id")
	int softDelete(@Param("id") Long id);

	public Tpa findByTpaName(String name);

	@Query(value = "SELECT COUNT(*) FROM TPA;", nativeQuery = true)
	Long countApplicationsByStatus();
}
