package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.UserProfileHeaderDAO;

public interface UserProfileHeaderRepository extends CommonRepository<UserProfileHeaderDAO, String> {

	List<UserProfileHeaderDAO> findByUserNameContainingAllIgnoringCase(String userName);
	
	@Query(value = "SELECT uph.* FROM EL_USER_PROFILE_HDR uph WHERE uph.USER_NAM NOT IN (SELECT DISTINCT urg.USER_NAM FROM EL_USER_ROLE_GROUP urg) AND uph.USER_NAM NOT IN (SELECT DISTINCT upd.USER_NAM FROM EL_USER_PROFILE_DTL upd)", nativeQuery = true)
    List<UserProfileHeaderDAO> findNotInUserRoleGroupAndUserProfileDetail();
	
	@Procedure(name = "set_user_nam")
    void setUserName(@Param("p_user_nam") String userName);
}
