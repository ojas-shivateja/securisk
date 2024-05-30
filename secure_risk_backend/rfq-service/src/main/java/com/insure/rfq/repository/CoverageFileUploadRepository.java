package com.insure.rfq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insure.rfq.entity.CoverageUploadEntity;

public interface CoverageFileUploadRepository extends JpaRepository<CoverageUploadEntity, Long> {

}
