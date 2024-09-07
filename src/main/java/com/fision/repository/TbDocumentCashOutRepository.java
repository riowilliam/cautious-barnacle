package com.fision.repository;

import com.fision.entity.TbDocumentCashOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbDocumentCashOutRepository extends JpaRepository<TbDocumentCashOut, Long> {
    TbDocumentCashOut findByDocumentName(String documentName);
}
