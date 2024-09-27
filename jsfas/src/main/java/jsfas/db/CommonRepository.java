package jsfas.db;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommonRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	/**
	 * find and lock the entity for update, could only use within transaction
	 * @param id
	 * @return DAO
	 */
	T findOneForUpdate(ID id);
	
	T findOne(ID id);
}
