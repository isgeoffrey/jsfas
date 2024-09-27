package jsfas.db.rbac.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.rbac.persistence.domain.EntityRelationshipDAO;
import jsfas.db.rbac.persistence.domain.EntityRelationshipDAOPK;

public interface EntityRelationshipRepository extends CommonRepository<EntityRelationshipDAO, EntityRelationshipDAOPK> {

    @Query("SELECT ea FROM EntityRelationshipDAO ea WHERE ea.entityRelationshipPK.entity.entityType = :entityType")
    List<EntityRelationshipDAO> findByEntityType(@Param("entityType") String entityType, Sort sort);
    
    @Query("SELECT ea FROM EntityRelationshipDAO ea WHERE ea.entityRelationshipPK.entity.entityType = :entityType AND ea.entityRelationshipPK.action.actionType = :actionType")
    Optional<EntityRelationshipDAO> findByEntityTypeAndActionType(@Param("entityType") String entityType, @Param("actionType") String actionType);
}
