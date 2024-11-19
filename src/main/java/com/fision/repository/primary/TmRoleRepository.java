package com.fision.repository.primary;

import com.fision.dto.RoleListDto;
import com.fision.entity.primary.TmRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TmRoleRepository extends JpaRepository<TmRole, Long> {
    TmRole findByRoleCode(String roleId);
    @Query("SELECT new com.fision.dto.RoleListDto ( rl.roleCode, rl.roleName, rl.permissions ) " +
            "FROM TmRole rl " +
            "WHERE rl.status = 1 ")
    List<RoleListDto> getRoleList();
}
