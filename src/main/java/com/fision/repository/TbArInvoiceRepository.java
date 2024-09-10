package com.fision.repository;

import com.fision.entity.TbArInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbArInvoiceRepository extends JpaRepository<TbArInvoice, Long> {

}
