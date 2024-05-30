package com.insure.rfq.repository;

import com.insure.rfq.entity.MyDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyDetailsRepository extends JpaRepository<MyDetailsEntity, Long> {
}
