package com.fision.repository;

import com.fision.entity.TbCashOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbCashOutRepository extends JpaRepository<TbCashOut, Long> {
}
