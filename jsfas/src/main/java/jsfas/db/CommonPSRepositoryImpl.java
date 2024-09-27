package jsfas.db;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import jsfas.security.SecurityUtils;

public class CommonPSRepositoryImpl<T, ID extends Serializable> extends CommonRepositoryImpl<T, ID> {
    private EntityManager entityManager;
    
    public CommonPSRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        
        // This is the recommended method for accessing inherited class dependencies.
        this.entityManager = entityManager;
    }
    
    // There are two constructors to choose from, either can be used.
    public CommonPSRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }
    
    private void setClientInfo() {
        Optional<String> optLoginAcctName = Optional.ofNullable(SecurityUtils.getCurrentLogin());
        
        if(optLoginAcctName.isPresent()) {
            entityManager.createStoredProcedureQuery("DBMS_APPLICATION_INFO.SET_CLIENT_INFO")
            	.registerStoredProcedureParameter("client_info", String.class, ParameterMode.IN)
            	.setParameter("client_info", optLoginAcctName.get());
        }
    }
    
    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        setClientInfo();
        return super.save(entity);
    }
    
    @Override
    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        setClientInfo();
        return super.saveAndFlush(entity);
    }
    
    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        setClientInfo();
        return super.saveAll(entities);
    }
    
    @Override
    @Transactional
    public void deleteById(ID id) {
        setClientInfo();
        super.deleteById(id);
    }
    
    @Override
    @Transactional
    public void delete(T entity) {
        setClientInfo();
        super.delete(entity);
    }
    
    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        setClientInfo();
        super.deleteAll(entities);
    }
    
    @Override
    @Transactional
    public void deleteInBatch(Iterable<T> entities) {
        setClientInfo();
        super.deleteInBatch(entities);
    }
    
    @Override
    @Transactional
    public void deleteAll() {
        setClientInfo();
        super.deleteAll();
    }
    
    @Override
    @Transactional
    public void deleteAllInBatch() {
        setClientInfo();
        super.deleteAllInBatch();
    }
}
