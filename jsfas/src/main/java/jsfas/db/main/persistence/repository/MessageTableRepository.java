package jsfas.db.main.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jsfas.db.main.persistence.domain.MessageTableDAO;

public interface MessageTableRepository extends JpaRepository<MessageTableDAO, String>{

	List<MessageTableDAO> findByMessageCodeAllIgnoringCase(String code);
	
}
