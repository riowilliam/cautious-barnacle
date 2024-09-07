package com.fision.repository;

import com.fision.entity.TbItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TbItemDetailsRepository extends JpaRepository<TbItemDetails, Long> {
    List<TbItemDetails> findByContractCodeAndRevision(String contractCode, Integer revision);

    TbItemDetails findByContractCodeAndRevisionAndItemId(String contractCode, Integer revision, Long itemId);
}
