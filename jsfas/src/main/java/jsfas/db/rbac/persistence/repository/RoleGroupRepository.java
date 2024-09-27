package jsfas.db.rbac.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.rbac.persistence.domain.RoleGroupDAO;

public interface RoleGroupRepository extends CommonRepository<RoleGroupDAO, String> {

    @Query("SELECT rg.parentRoleGroup FROM RoleGroupDAO rg WHERE rg.roleGroupId = :roleGroupId AND rg.parentRoleGroup IS NOT NULL")
    Optional<RoleGroupDAO> findParentRoleGroupByRoleGroupId(@Param("roleGroupId") String roleGroupId);
    
    @Query("SELECT rg FROM RoleGroupDAO rg WHERE rg.parentRoleGroup.roleGroupId = :parentRoleGroupId")
    List<RoleGroupDAO> findByParentRoleGroupId(@Param("parentRoleGroupId") String parentRoleGroupId, Sort sort);
    
    @Query("SELECT rg FROM RoleGroupDAO rg WHERE rg.roleGroupId = :roleGroupId")
    List<RoleGroupDAO> findByRoleGroupId(@Param("roleGroupId") String roleGroupId, Sort sort);
    
    @Query("SELECT rg FROM RoleGroupDAO rg WHERE rg.roleGroupId = :roleGroupId AND rg.parentRoleGroup IS NULL")
    Optional<RoleGroupDAO> findRootRoleGroupByRoleGroupId(@Param("roleGroupId") String roleGroupId);
    
    @Query("SELECT rg FROM RoleGroupDAO rg WHERE rg.parentRoleGroup IS NULL")
    List<RoleGroupDAO> findRootRoleGroup(Sort sort);
}
