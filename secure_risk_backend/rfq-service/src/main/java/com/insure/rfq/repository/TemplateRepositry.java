package com.insure.rfq.repository;

import com.insure.rfq.entity.TemplateDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TemplateRepositry extends JpaRepository<TemplateDetails, Long> {

    List<TemplateDetails> findByTemplateType(String type);

    List<TemplateDetails> findByType(String type);

    @Query("SELECT t FROM TemplateDetails t WHERE t.product.productId = :productId")
    List<TemplateDetails> findByProductId(Long productId);

}
