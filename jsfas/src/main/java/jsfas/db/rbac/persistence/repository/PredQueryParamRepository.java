package jsfas.db.rbac.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jsfas.db.rbac.persistence.domain.PredQueryParamDAO;

public interface PredQueryParamRepository extends JpaRepository<PredQueryParamDAO, String> {

}
