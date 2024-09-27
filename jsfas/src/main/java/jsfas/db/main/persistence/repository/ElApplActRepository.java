package jsfas.db.main.persistence.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jsfas.db.CommonRepository;
import jsfas.db.main.persistence.domain.ElApplActDAO;

public interface ElApplActRepository extends CommonRepository<ElApplActDAO, String> {

	@Query(value = "SELECT * FROM EL_APPL_ACT WHERE appl_hdr_id = :appl_hdr_id ORDER BY action_dttm ", nativeQuery = true)
	List<ElApplActDAO> findByApplHdrId(@Param("appl_hdr_id") String applHdrId);

	@Query(value = ""
			+ "WITH base as ( "
			+ "    SELECT a.* FROM EL_APPL_ACT a "
			+ "    INNER JOIN ( "
			+ "        SELECT * FROM EL_APPL_ACT WHERE appl_hdr_id = :appl_hdr_id AND ACTION = 'Issue Offer' "
			+ "    ) b on a.appl_hdr_id = b.appl_hdr_id  "
			+ "    WHERE  "
			+ "        a.ACTION_DTTM < b.ACTION_DTTM  "
			+ ") "
			+ "SELECT distinct action_by FROM base WHERE base.action_dttm >= (SELECT MAX(action_dttm) FROM base WHERE action = '" + ElApplActDAO.SUBMIT_APPL + "') ", nativeQuery = true)
	List<String> findPastApproversBeforeReleaseLOA(@Param("appl_hdr_id") String applHdrId);
	
	@Query(value = "SELECT action FROM EL_APPL_ACT WHERE appl_hdr_id = :appl_hdr_id ORDER BY action_dttm ", nativeQuery = true)
	List<String> findActionByApplHdrId(@Param("appl_hdr_id") String applHdrId);
	
	@Query(value = ""
			+ "SELECT a.appl_hdr_id FROM el_appl_act a "
			+ "INNER JOIN                "
			+ "( SELECT "
			+ "                    appl_hdr_id, "
			+ "                    MAX(action_dttm) maxSubmitDt "
			+ "                FROM "
			+ "                    el_appl_act "
			+ "                WHERE "
			+ "                    action = '"+ ElApplActDAO.SUBMIT_APPL + "' "
			+ "                GROUP BY  "
			+ "                    appl_hdr_id "
			+ ") b ON a.appl_hdr_id = b.appl_hdr_id AND a.action_dttm >= maxSubmitDt "
			+ "WHERE "
			+ "a.action_by = :remoteUser" , nativeQuery = true)
	List<String> findRemoteUserApprovedAppl(@Param("remoteUser") String remoteUser);

	@Query(value = "SELECT appl_hdr_id FROM ( "
			+ " SELECT max(action_dttm) action_dttm, appl_hdr_id FROM EL_APPL_ACT "
			+ " WHERE appl_hdr_id in :appl_hdr_id_list "
			+ " GROUP BY appl_hdr_id "
			+ " ) "
			+ " WHERE action_dttm BETWEEN :start_time AND :end_time ", nativeQuery = true)
	List<String> findLastActionByApplHdrIdAndTimePeriod(@Param("appl_hdr_id_list") List<String> applHdrIdList, @Param("start_time") Timestamp startTime, @Param("end_time") Timestamp endTime );
}
