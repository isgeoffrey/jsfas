package jsfas.db.rbac.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.rbac.persistence.domain.PermissionDAO;
import jsfas.db.rbac.persistence.domain.PermissionDAOPK;

public interface PermissionRepository extends CommonRepository<PermissionDAO, PermissionDAOPK> {
    
    @Query("SELECT p FROM PermissionDAO p WHERE p.permissionPK.roleGroup.roleGroupId = :roleGroupId AND p.permissionPK.entity.entityType = :entityType AND p.permissionPK.action.actionType = :actionType")
    Optional<PermissionDAO> findByRoleGroupIdAndEntityTypeAndActionType(@Param("roleGroupId") String roleGroupId, @Param("entityType") String entityType, @Param("actionType") String actionType);
    
    @Query("SELECT p FROM PermissionDAO p WHERE p.permissionPK.roleGroup.roleGroupId IN :roleGroupIdList AND p.permissionPK.entity.entityType = :entityType AND p.permissionPK.action.actionType = :actionType")
    List<PermissionDAO> findByRoleGroupIdInAndEntityTypeAndActionType(@Param("roleGroupIdList") List<String> roleGroupIdList, @Param("entityType") String entityType, @Param("actionType") String actionType);
    
    @Query("SELECT p FROM PermissionDAO p WHERE p.permissionPK.roleGroup.roleGroupId IN :roleGroupIdList")
    List<PermissionDAO> findByRoleGroupIdIn(@Param("roleGroupIdList") Set<String> roleGroupIdSet, Sort sort);
    
    @Query("SELECT p FROM PermissionDAO p WHERE p.expiryDate > CURRENT_TIMESTAMP AND p.permStatus.permStatusType = :permStatusType")
    List<PermissionDAO> findExpireByPermStatusType(@Param("permStatusType") String permStatusType, Sort sort);
    
    @Query("SELECT p FROM PermissionDAO p WHERE p.effectiveDate <= CURRENT_TIMESTAMP AND p.permStatus.permStatusType = :permStatusType")
    List<PermissionDAO> findActiveByPermStatusType(@Param("permStatusType") String permStatusType, Sort sort);
    
}
