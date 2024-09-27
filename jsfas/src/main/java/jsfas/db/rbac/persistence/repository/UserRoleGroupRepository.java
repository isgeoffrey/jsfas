package jsfas.db.rbac.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.rbac.persistence.domain.UserRoleGroupDAO;
import jsfas.db.rbac.persistence.domain.UserRoleGroupDAOPK;

public interface UserRoleGroupRepository extends CommonRepository<UserRoleGroupDAO, UserRoleGroupDAOPK> {

    @Query("SELECT urg FROM UserRoleGroupDAO urg WHERE urg.userRoleGroupPK.userProfileHeader.userName = :username")
    List<UserRoleGroupDAO> findByUsername(@Param("username") String username, Sort sort);
    
    @Query("SELECT count(urg) FROM UserRoleGroupDAO urg WHERE urg.userRoleGroupPK.userProfileHeader.userName = :username")
    Long countByUsername(@Param("username") String username);
    
    @Query("SELECT urg FROM UserRoleGroupDAO urg WHERE urg.userRoleGroupPK.userProfileHeader.userName = :username AND urg.userRoleGroupPK.roleGroup.roleGroupId = :roleGroupId")
    List<UserRoleGroupDAO> findByUsernameAndRoleGroupId(@Param("username") String username, @Param("roleGroupId") String roleGroupId, Sort sort);
    
    @Query("SELECT count(urg) FROM UserRoleGroupDAO urg WHERE urg.userRoleGroupPK.userProfileHeader.userName = :username AND urg.userRoleGroupPK.roleGroup.roleGroupId = :roleGroupId")
    Long countByUsernameAndRoleGroupId(@Param("username") String username, @Param("roleGroupId") String roleGroupId);
    
    @Query("SELECT urg FROM UserRoleGroupDAO urg WHERE urg.userRoleGroupPK.roleGroup.roleGroupId = :roleGroupId")
    List<UserRoleGroupDAO> findByRoleGroupId(@Param("roleGroupId") String roleGroupId, Sort sort);

    @Query(value = ""
    		+ "SELECT distinct user_nam FROM FAS_USER_ROLE_GROUP urg  "
    		+ "INNER JOIN FAS_ROLE_GROUP rg ON urg.role_group_id = rg.role_group_id "
    		+ "where UPPER(role_group_desc) = UPPER(:role_group_desc) ", nativeQuery = true)
    List<String> findByRoleGroupDesc(@Param("role_group_desc") String role_group_desc);
}
