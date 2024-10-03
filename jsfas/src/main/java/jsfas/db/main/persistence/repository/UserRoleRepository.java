package jsfas.db.main.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jsfas.db.main.persistence.domain.UserRoleDAO;
import jsfas.db.main.persistence.domain.UserRoleDAOPK;

public interface UserRoleRepository extends JpaRepository<UserRoleDAO, UserRoleDAOPK> {

}
