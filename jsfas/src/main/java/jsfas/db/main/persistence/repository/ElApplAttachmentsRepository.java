package jsfas.db.main.persistence.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplAttachmentsDAO;

public interface ElApplAttachmentsRepository extends CommonRepository<ElApplAttachmentsDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_ATTACHMENTS WHERE file_category = :fileCatg AND appl_hdr_id = :applHdrId AND id NOT IN :idList ", nativeQuery = true)
	List<ElApplAttachmentsDAO> findByApplHdrIdAndFileCategoryForRemove(@Param("applHdrId") String applHdrId, @Param("fileCatg")String fileCatg, @Param("idList") List<String> upsertDAOId);

	List<ElApplAttachmentsDAO> findByApplHdrIdAndFileCategory(String applHdrId, String fileCategory);
	
	@Query(value = ""
			+ "SELECT a.id el_appl_attachments_id, a.file_id, a.file_category, a.mod_ctrl_txt, f.file_name  "
			+ "FROM EL_APPL_ATTACHMENTS a "
			+ "JOIN EL_UPLD_FILE f ON a.file_id = f.id "
			+ "WHERE file_category = :fileCatg AND appl_hdr_id = :applHdrId ", nativeQuery = true)
	List<Map<String, String>> findDetailsByApplHdrIdAndFileCategory(@Param("applHdrId")String applHdrId, @Param("fileCatg")String fileCatg);
}
