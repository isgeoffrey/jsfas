package jsfas.db.main.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;

public interface FasStkPlanDtlRepository extends JpaRepository<FasStkPlanDtlDAO, FasStkPlanDtlDAOPK> {

}
