package jsfas.db.rbac.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.rbac.persistence.domain.EntityDAO;

public interface EntityRepository extends CommonRepository<EntityDAO, String> {
    
    @Query("SELECT e.parentEntity FROM EntityDAO e WHERE e.entityType = :entityType AND e.parentEntity IS NOT NULL")
    Optional<EntityDAO> findParentEntityByEntityType(@Param("entityType") String entityType);
    
    @Query("SELECT e FROM EntityDAO e WHERE e.parentEntity.entityType = :parentEntityType")
    List<EntityDAO> findByParentEntityType(@Param("parentEntityType") String parentEntityType, Sort sort);
    
    @Query("SELECT e FROM EntityDAO e WHERE e.parentEntity IS NULL")
    List<EntityDAO> findRootEntity(Sort sort);
}
