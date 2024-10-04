package jsfas.db.main.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.UserFuncVDAO;
import jsfas.db.main.persistence.domain.UserFuncVDAOPK;

public interface UserFuncVRepository extends CommonRepository<UserFuncVDAO, UserFuncVDAOPK> {

}
