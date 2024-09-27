package jsfas.db.main.persistence.repository;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElProjChrtVDAO;

public interface ElProjChrtVRepository extends CommonRepository<ElProjChrtVDAO, String> {
	
	ElProjChrtVDAO findByProjId(String projId);

}
