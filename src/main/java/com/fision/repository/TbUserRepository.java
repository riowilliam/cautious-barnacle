package com.fision.repository;

import com.fision.dto.UserListDto;
import com.fision.entity.TbUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author LordDev
 */
@Repository
public interface TbUserRepository extends JpaRepository<TbUser, Long> {
    TbUser findByUsername(String username);
    TbUser findByEmail(String email);

    @Query("SELECT new com.fision.dto.UserListDto ( " +
            "usr.username, usr.fullName, usr.email, " +
            "rl.roleName, usr.contact, " +
            "usr.createdTm, usr.createdBy, " +
            "usr.modifiedTm, usr.modifiedBy) " +
            "FROM TbUser usr " +
            "LEFT JOIN TmRole rl ON usr.roleCode = rl.roleCode " +
            "WHERE (:fullName IS NULL OR usr.fullName LIKE %:fullName%) " +
            "AND (:email IS NULL OR usr.email LIKE %:email%) " +
            "AND (:roleCode IS NULL OR usr.roleCode LIKE %:roleCode%) " +
            "AND (:contact IS NULL OR usr.contact LIKE %:contact%) ")
    Page<UserListDto> getUserListPaging(@Param("fullName") String fullName,
                                        @Param("email") String email,
                                        @Param("roleCode") String roleCode,
                                        @Param("contact") String contact,
                                        Pageable pageable);
}
