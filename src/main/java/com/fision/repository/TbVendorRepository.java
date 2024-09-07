package com.fision.repository;

import com.fision.entity.TbVendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TbVendorRepository extends JpaRepository<TbVendor, Long> {
    Optional<TbVendor> findByVendorName(String vendorName);

    @Query("SELECT v.vendorId as vendorId, v.vendorName as vendorName " +
            "FROM TbVendor v " +
            "WHERE (:vendorName IS NULL OR v.vendorName LIKE %:vendorName%) ")
    List<Map<String, Object>> getVendorList(@Param("vendorName") String vendorName);

    @Query("SELECT v FROM TbVendor v " +
            "WHERE (:vendorName IS NULL OR v.vendorName LIKE %:vendorName%) " +
            "AND (:bankName IS NULL OR v.bankName LIKE %:bankName%) " +
            "AND (:bankAccount IS NULL OR v.bankAccount LIKE %:bankAccount%) " +
            "AND (:bankAccountName IS NULL OR v.bankAccountName LIKE %:bankAccountName%) ")
    Page<TbVendor> getVendorListPaging(@Param("vendorName") String vendorName,
                                       @Param("bankName") String bankName,
                                       @Param("bankAccount") String bankAccount,
                                       @Param("bankAccountName") String bankAccountName,
                                       Pageable pageable);
}
