package jsfas.db;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

public class CommonRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CommonRepository<T, ID> {
	private EntityManager entityManager;
	private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

	public CommonRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
	    super(entityInformation, entityManager);
	    
	    // This is the recommended method for accessing inherited class dependencies.
	    this.entityManager = entityManager;
	}
	
	// There are two constructors to choose from, either can be used.
	public CommonRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
	    this(JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager), entityManager);
	}

	public T findOneForUpdate(ID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		Class<T> domainType = getDomainClass();
		return entityManager.find(domainType, id, LockModeType.PESSIMISTIC_WRITE);
	}

	@Override
	public T findOne(ID id) {
	    return findById(id).orElse(null);
	}
}
