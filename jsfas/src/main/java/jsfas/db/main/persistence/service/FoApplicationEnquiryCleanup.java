package jsfas.db.main.persistence.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jsfas.common.constants.PymtStatusConstants;
import jsfas.common.constants.PymtTypeConstants;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FoApplicationEnquiryComplete;
import jsfas.db.main.persistence.domain.FoApplicationEnquiryRaw;
import jsfas.db.main.persistence.repository.ElApplPymtScheduleRepository;

//@Component
//@DependsOn({"elApplPymtScheduleRepository"})
public class FoApplicationEnquiryCleanup {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    private List<FoApplicationEnquiryRaw> foApplicationEnquiryRawList;
    private HashMap<String, FoApplicationEnquiryComplete> recurrHash;
    private HashMap<String, FoApplicationEnquiryComplete> instalHash;
    
    private ElApplPymtScheduleRepository elApplPymtScheduleRepository;
//    @Autowired
//	ElApplPymtScheduleRepository elApplPymtScheduleRepository;
    
    public FoApplicationEnquiryCleanup(List<FoApplicationEnquiryRaw> foApplicationEnquiryRawList, ElApplPymtScheduleRepository elApplPymtScheduleRepository) {
        this.foApplicationEnquiryRawList = foApplicationEnquiryRawList;
        this.recurrHash = new HashMap<>();
        this.instalHash = new HashMap<>();
        this.elApplPymtScheduleRepository = elApplPymtScheduleRepository;
    }

    public void parseAndAddToHash() {
    	log.info("parsing arrayList to hash");
    	
    	
        for (FoApplicationEnquiryRaw foApplicationEnquiryRaw : foApplicationEnquiryRawList) {
        	
        	HashMap<String, FoApplicationEnquiryComplete> targetMap;
        	
//        	if (foApplicationEnquiryRaw.getPymtTypeCde())
        	if(PymtTypeConstants.INSTALM.equalsIgnoreCase(foApplicationEnquiryRaw.getPymtTypeCde())) {
        		targetMap = instalHash;
        	}else if (PymtTypeConstants.RECURR.equalsIgnoreCase(foApplicationEnquiryRaw.getPymtTypeCde())) {
        		targetMap = recurrHash;
        	}else {
        		log.error("Application "+foApplicationEnquiryRaw.getAppId() +" Does not have valid Paymtent Type Code");
        		continue;
        	}
        	
            String appNo = foApplicationEnquiryRaw.getAppId();
            String aprvTypeCde = foApplicationEnquiryRaw.getAprvTypeCde();
            Boolean approved = foApplicationEnquiryRaw.getApproved() == 1;    

			String newFoApprver = WordUtils.capitalizeFully(GeneralUtil.initNullString(foApplicationEnquiryRaw.getAprvInpostName())) +" "+"("+foApplicationEnquiryRaw.getAprvUserId()+")";
            String newOtherApprver = WordUtils.capitalizeFully(GeneralUtil.initNullString(foApplicationEnquiryRaw.getAprvUserName()))+" "+"("+foApplicationEnquiryRaw.getAprvUserId()+")";

            FoApplicationEnquiryComplete foApplicationEnquiryComplete = targetMap.getOrDefault(appNo, new FoApplicationEnquiryComplete(foApplicationEnquiryRaw));
            
            if ("FO_SR".equals(aprvTypeCde)) {
            	if (GeneralUtil.isBlankString(GeneralUtil.initNullString(foApplicationEnquiryComplete.getFoSR()))) {
            		foApplicationEnquiryComplete.setFoSR(newFoApprver);
                	if (approved && !GeneralUtil.isNullTimestamp(foApplicationEnquiryRaw.getAprvDttm())) {
                		foApplicationEnquiryComplete.setFoSRAprvDate(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2"));
                	}
            	}else {
            		foApplicationEnquiryComplete.setFoSR(foApplicationEnquiryComplete.getFoSR()+"; "+ newFoApprver);
            		foApplicationEnquiryComplete.setFoSRAprvDate(GeneralUtil.initBlankString(foApplicationEnquiryComplete.getFoSRAprvDate())+"; "+ GeneralUtil.initBlankString(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2")));
            	}
            } else if ("FO_SFM".equals(aprvTypeCde)) {
                foApplicationEnquiryComplete.setFoSFM(newFoApprver);
                if (approved && !GeneralUtil.isNullTimestamp(foApplicationEnquiryRaw.getAprvDttm())) {
                	foApplicationEnquiryComplete.setFoSFMAprvDate(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2"));	
                }
                
            } else if ("FO_PNB".equals(aprvTypeCde)) {
                foApplicationEnquiryComplete.setFoPB(newFoApprver);
                if (approved && !GeneralUtil.isNullTimestamp(foApplicationEnquiryRaw.getAprvDttm())) {
                	foApplicationEnquiryComplete.setFoPBAprvDate(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2"));
                }
                
            } else if ("DEPT_HEAD".equals(aprvTypeCde)) {
                foApplicationEnquiryComplete.setDeptHead(newOtherApprver);
                if (approved && !GeneralUtil.isNullTimestamp(foApplicationEnquiryRaw.getAprvDttm())) {
                	foApplicationEnquiryComplete.setDeptHeadAprvDate(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2"));
                }
                
            } else if ("BCO".equals(aprvTypeCde) || "BCO_APPL".equals(aprvTypeCde) || "BCO_PYMT".equals(aprvTypeCde)) {
            	if (GeneralUtil.isBlankString(GeneralUtil.initNullString(foApplicationEnquiryComplete.getBco()))) { // no prior value
            		foApplicationEnquiryComplete.setBco(newFoApprver); // directly add id to bco field 
                	if (approved && !GeneralUtil.isNullTimestamp(foApplicationEnquiryRaw.getAprvDttm())) {
                		foApplicationEnquiryComplete.setBcoAprvDate(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2"));
                	}
            	}else {
            		foApplicationEnquiryComplete.setBco(GeneralUtil.initBlankString(foApplicationEnquiryComplete.getBco())+"; "+ newFoApprver);
            		foApplicationEnquiryComplete.setBcoAprvDate(GeneralUtil.initBlankString(foApplicationEnquiryComplete.getBcoAprvDate())+"; "+ GeneralUtil.initBlankString(GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2")));
//            		tempFoEnquiryDAOComplete.setBcoAprvDate("dllm");
            	}
            	
            } else if ("AD_HOC".equals(aprvTypeCde)) {
            	String aprvr = foApplicationEnquiryRaw.getAprvUserId();            	
            	if (approved && !GeneralUtil.isNullTimestamp(foApplicationEnquiryRaw.getAprvDttm())) {
            		foApplicationEnquiryComplete.setNextAdHocValues(newOtherApprver, GeneralUtil.convertTimestampToString(foApplicationEnquiryRaw.getAprvDttm(), "N2"));
            	}else {
            		foApplicationEnquiryComplete.setNextAdHocValues(newOtherApprver);
            	}
            }
            
            targetMap.put(appNo, foApplicationEnquiryComplete);
        }
        log.info("end parsing arrayList to hash");
    }
    
    
    public JSONArray convertHashToJSONArray() throws JSONException {
    	 log.info("converting hashmap to JSONArray");
    	JSONArray jsonArray = new JSONArray();
    	
    	// assign values for recurring payment applications
    	 for (FoApplicationEnquiryComplete daoComplete : recurrHash.values()) {
             JSONObject jsonObject = new JSONObject();
         	// assignApplicationDetails
             jsonObject.put("appId", daoComplete.getAppId());
             jsonObject.put("appNbr", daoComplete.getAppNbr());
             jsonObject.put("brNo", daoComplete.getBrNo());
             jsonObject.put("versionNo", daoComplete.getVersionNo());
             jsonObject.put("creatDat", daoComplete.getCreatDat());
             jsonObject.put("chngDat", daoComplete.getChngDat());
             jsonObject.put("applDttm", daoComplete.getApplDttm());
             jsonObject.put("applStatCde", daoComplete.getApplStatCde());
             jsonObject.put("applStatDescr", daoComplete.getApplStatDescr());
             jsonObject.put("appAmt", daoComplete.getAppAmt());
             if (!GeneralUtil.isNullTimestamp(daoComplete.getApplStartDt())) {
            	 jsonObject.put("applStartDt", daoComplete.getApplStartDt());
             }
             if (!GeneralUtil.isNullTimestamp(daoComplete.getApplEndDt())) {
            	 jsonObject.put("applEndDt", daoComplete.getApplEndDt());
             }
             
             
          // assignRequesterAndApplicantDetails
             jsonObject.put("applRequesterDept", daoComplete.getApplRequesterDept());
             jsonObject.put("applRequesterName", daoComplete.getApplRequesterName());
             jsonObject.put("applRequesterId", daoComplete.getApplRequesterId());
             jsonObject.put("applUserId", daoComplete.getApplUserId());
             jsonObject.put("applUserName", daoComplete.getApplUserName());
             jsonObject.put("applUserDept", daoComplete.getApplUserDept());
             jsonObject.put("applSchlShort", daoComplete.getApplSchlShort());
             jsonObject.put("applSchlLong", daoComplete.getApplSchlLong());
             jsonObject.put("reqSchlShort", daoComplete.getReqSchlShort());
             jsonObject.put("reqSchlLong", daoComplete.getReqSchlLong());

          // assignPendingApproverDetails
             jsonObject.put("pendingAprverId", daoComplete.getPendingAprverId());
             jsonObject.put("pendingAprverName", daoComplete.getPendingAprverName());
             jsonObject.put("pendingAprverType", daoComplete.getPendingAprverType());
             
          // assignCourseDetails
             jsonObject.put("elTypeNam", daoComplete.getElTypeNam());
             jsonObject.put("applUserIdCount", daoComplete.getApplUserIdCount());
             jsonObject.put("applStartTerm", daoComplete.getApplStartTerm());
             jsonObject.put("applEndTerm", daoComplete.getApplEndTerm());
             jsonObject.put("applStartTermStrm", daoComplete.getApplStartTermStrm());
             jsonObject.put("applEndTermStrm", daoComplete.getApplEndTermStrm());
             jsonObject.put("prgmCde", daoComplete.getPrgmCde());
             jsonObject.put("schCde", daoComplete.getSchCde());
             jsonObject.put("dept", daoComplete.getDept());
             
          // assign payment details
             jsonObject.put("appAmt", daoComplete.getAppAmt());
             jsonObject.put("pymtAmt", daoComplete.getPymtAmt());
             jsonObject.put("mpfAmt", daoComplete.getMpfAmt());
             jsonObject.put("pymtTypeCde", daoComplete.getPymtTypeCde());
             jsonObject.put("categoryDescr", daoComplete.getCategorDescr());

             if (!GeneralUtil.isNullTimestamp(daoComplete.getPymtStartDt())) {
            	 jsonObject.put("pymtStartDt", daoComplete.getPymtStartDt());
             }
             if (!GeneralUtil.isNullTimestamp(daoComplete.getPymtEndDt())) {
            	 jsonObject.put("pymtEndDt", daoComplete.getPymtEndDt());
             }
             
          // assign approver details
             jsonObject.put("deptHead", daoComplete.getDeptHead());
             jsonObject.put("deptHeadAprvDate", daoComplete.getDeptHeadAprvDate());
             jsonObject.put("bco", daoComplete.getBco());
             jsonObject.put("bcoAprvDate", daoComplete.getBcoAprvDate());
             jsonObject.put("adHoc1", daoComplete.getAdHoc1());
             jsonObject.put("adHoc1AprvDate", daoComplete.getAdHoc1AprvDate());
             jsonObject.put("adHoc2", daoComplete.getAdHoc2());
             jsonObject.put("adHoc2AprvDate", daoComplete.getAdHoc2AprvDate());
             jsonObject.put("adHoc3", daoComplete.getAdHoc3());
             jsonObject.put("adHoc3AprvDate", daoComplete.getAdHoc3AprvDate());
             jsonObject.put("adHoc4", daoComplete.getAdHoc4());
             jsonObject.put("adHoc4AprvDate", daoComplete.getAdHoc4AprvDate());
             jsonObject.put("adHoc5", daoComplete.getAdHoc5());
             jsonObject.put("adHoc5AprvDate", daoComplete.getAdHoc5AprvDate());
             jsonObject.put("foSR", daoComplete.getFoSR());
             jsonObject.put("foSRAprvDate", daoComplete.getFoSRAprvDate());
             jsonObject.put("foSFM", daoComplete.getFoSFM());
             jsonObject.put("foSFMAprvDate", daoComplete.getFoSFMAprvDate());
             jsonObject.put("foPB", daoComplete.getFoPB());
             jsonObject.put("foPBAprvDate", daoComplete.getFoPBAprvDate());
             
             jsonArray.put(jsonObject);
         }
    	 
    	 // assign values for installment payment applications
    	 for (FoApplicationEnquiryComplete daoComplete : instalHash.values()) {
    		 List<Map<String, Object>> pymtSched = elApplPymtScheduleRepository.findSelectedByApplHdrIdWithSchedNoPymt(daoComplete.getAppId());
    		 for (Map<String, Object> pymt: pymtSched) {
    			 JSONObject jsonObject = new JSONObject();
    			 
    			// assignApplicationDetails
                 jsonObject.put("appId", daoComplete.getAppId());
                 jsonObject.put("appNbr", daoComplete.getAppNbr());
                 jsonObject.put("brNo", daoComplete.getBrNo());
                 jsonObject.put("versionNo", daoComplete.getVersionNo());
                 jsonObject.put("creatDat", daoComplete.getCreatDat());
                 jsonObject.put("chngDat", daoComplete.getChngDat());
                 jsonObject.put("applDttm", daoComplete.getApplDttm());
                 jsonObject.put("applStatCde", daoComplete.getApplStatCde());
                 jsonObject.put("applStatDescr", daoComplete.getApplStatDescr());
                 jsonObject.put("appAmt", daoComplete.getAppAmt());
                 if (!GeneralUtil.isNullTimestamp(daoComplete.getApplStartDt())) {
                	 jsonObject.put("applStartDt", daoComplete.getApplStartDt());
                 }
                 if (!GeneralUtil.isNullTimestamp(daoComplete.getApplEndDt())) {
                	 jsonObject.put("applEndDt", daoComplete.getApplEndDt());
                 }
                 
                 
              // assignRequesterAndApplicantDetails
                 jsonObject.put("applRequesterDept", daoComplete.getApplRequesterDept());
                 jsonObject.put("applRequesterName", daoComplete.getApplRequesterName());
                 jsonObject.put("applRequesterId", daoComplete.getApplRequesterId());
                 jsonObject.put("applUserId", daoComplete.getApplUserId());
                 jsonObject.put("applUserName", daoComplete.getApplUserName());
				 jsonObject.put("applUserDept", daoComplete.getApplUserDept());
				 jsonObject.put("applSchlShort", daoComplete.getApplSchlShort());
	             jsonObject.put("applSchlLong", daoComplete.getApplSchlLong());
	             jsonObject.put("reqSchlShort", daoComplete.getReqSchlShort());
	             jsonObject.put("reqSchlLong", daoComplete.getReqSchlLong());
                 
              // assignPendingApproverDetails
                 jsonObject.put("pendingAprverId", daoComplete.getPendingAprverId());
                 jsonObject.put("pendingAprverName", daoComplete.getPendingAprverName());
                 jsonObject.put("pendingAprverType", daoComplete.getPendingAprverType());
                 
              // assignCourseDetails
                 jsonObject.put("elTypeNam", daoComplete.getElTypeNam());
                 jsonObject.put("applUserIdCount", daoComplete.getApplUserIdCount());
                 jsonObject.put("applStartTerm", daoComplete.getApplStartTerm());
                 jsonObject.put("applEndTerm", daoComplete.getApplEndTerm());
                 jsonObject.put("applStartTermStrm", daoComplete.getApplStartTermStrm());
                 jsonObject.put("applEndTermStrm", daoComplete.getApplEndTermStrm());
                 jsonObject.put("prgmCde", daoComplete.getPrgmCde());
                 jsonObject.put("schCde", daoComplete.getSchCde());
                 jsonObject.put("dept", daoComplete.getDept());
                 
              // assign payment details
                 jsonObject.put("categoryDescr", daoComplete.getCategorDescr());
                 jsonObject.put("appAmt", daoComplete.getAppAmt());
                 jsonObject.put("mpfAmt", daoComplete.getMpfAmt());
                 jsonObject.put("pymtTypeCde", daoComplete.getPymtTypeCde());
                 
                 jsonObject.put("pymtAmt", pymt.get("TOTAL_SUM"));
//                 System.out.println(daoComplete.getAppNbr()+" => "+pymt.get("PYMT_SCHED_NO")+": "+pymt.get("TOTAL_SUM"));
    			 jsonObject.put("instalmNo", daoComplete.getInstalmNo());
                 jsonObject.put("instalmSeq", pymt.get("PYMT_SCHED_NO"));
                 jsonObject.put("pymtStatusCde", pymt.get("PYMT_STATUS_CDE"));
                 
                 if (!PymtStatusConstants.POST.equals(pymt.get("PYMT_STATUS_CDE")) || "system".equalsIgnoreCase(daoComplete.getPendingAprverType())) {
                	
                	// assign approver details
                     jsonObject.put("deptHead", daoComplete.getDeptHead());
                     jsonObject.put("deptHeadAprvDate", daoComplete.getDeptHeadAprvDate());
                     jsonObject.put("bco", daoComplete.getBco());
                     jsonObject.put("bcoAprvDate", daoComplete.getBcoAprvDate());
                     jsonObject.put("adHoc1", daoComplete.getAdHoc1());
                     jsonObject.put("adHoc1AprvDate", daoComplete.getAdHoc1AprvDate());
                     jsonObject.put("adHoc2", daoComplete.getAdHoc2());
                     jsonObject.put("adHoc2AprvDate", daoComplete.getAdHoc2AprvDate());
                     jsonObject.put("adHoc3", daoComplete.getAdHoc3());
                     jsonObject.put("adHoc3AprvDate", daoComplete.getAdHoc3AprvDate());
                     jsonObject.put("adHoc4", daoComplete.getAdHoc4());
                     jsonObject.put("adHoc4AprvDate", daoComplete.getAdHoc4AprvDate());
                     jsonObject.put("adHoc5", daoComplete.getAdHoc5());
                     jsonObject.put("adHoc5AprvDate", daoComplete.getAdHoc5AprvDate());
                     jsonObject.put("foSR", daoComplete.getFoSR());
                     jsonObject.put("foSRAprvDate", daoComplete.getFoSRAprvDate());
                     jsonObject.put("foSFM", daoComplete.getFoSFM());
                     jsonObject.put("foSFMAprvDate", daoComplete.getFoSFMAprvDate());
                     jsonObject.put("foPB", daoComplete.getFoPB());
                     jsonObject.put("foPBAprvDate", daoComplete.getFoPBAprvDate());
                 }
                 
    			 jsonArray.put(jsonObject);
    		 }
    	 }
    	 
    	 log.info("JSONArray successfully generated");
    	 return jsonArray;
    }
 
}