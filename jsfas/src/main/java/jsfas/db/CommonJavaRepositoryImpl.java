package jsfas.db;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import jsfas.security.SecurityUtils;

public class CommonJavaRepositoryImpl<T, ID extends Serializable> extends CommonRepositoryImpl<T, ID> {
    private EntityManager entityManager;
    
    public CommonJavaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        
        // This is the recommended method for accessing inherited class dependencies.
        this.entityManager = entityManager;
    }
    
    // There are two constructors to choose from, either can be used.
    public CommonJavaRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }
    
    private void setSecurityInfoPackage() {
        Optional<String> optLoginAcctName = Optional.ofNullable(SecurityUtils.getCurrentLogin());
        
        if(optLoginAcctName.isPresent()) {
        	entityManager.createStoredProcedureQuery("SEC_INFO_PKG.SET_USER_NAM")
				.registerStoredProcedureParameter("p_user_nam", String.class, ParameterMode.IN)
				.setParameter("p_user_nam", optLoginAcctName.get());
        }
    }
    
    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        setSecurityInfoPackage();
        return super.save(entity);
    }
    
    @Override
    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        setSecurityInfoPackage();
        return super.saveAndFlush(entity);
    }
    
    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        setSecurityInfoPackage();
        return super.saveAll(entities);
    }
    
    @Override
    @Transactional
    public void deleteById(ID id) {
        setSecurityInfoPackage();
        super.deleteById(id);
    }
    
    @Override
    @Transactional
    public void delete(T entity) {
        setSecurityInfoPackage();
        super.delete(entity);
    }
    
    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        setSecurityInfoPackage();
        super.deleteAll(entities);
    }
    
    @Override
    @Transactional
    public void deleteInBatch(Iterable<T> entities) {
        setSecurityInfoPackage();
        super.deleteInBatch(entities);
    }
    
    @Override
    @Transactional
    public void deleteAll() {
        setSecurityInfoPackage();
        super.deleteAll();
    }
    
    @Override
    @Transactional
    public void deleteAllInBatch() {
        setSecurityInfoPackage();
        super.deleteAllInBatch();
    }
}
