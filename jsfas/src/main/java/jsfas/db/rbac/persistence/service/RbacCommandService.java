package jsfas.db.rbac.persistence.service;

import java.util.List;
import java.util.Map;

import org.apache.shiro.subject.Subject;

import jsfas.common.json.CommonJson;
import jsfas.db.rbac.persistence.domain.RoleGroupDAO;
import jsfas.security.RbacCmdType;

public interface RbacCommandService {

    public void processBatch() throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void processCommandList(List commandList, String opPageName) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void processCommandList(List commandList, String opPageName, boolean checkPermission) throws Exception;
    
    public void processCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName, boolean checkPermission) throws Exception;
    
    public void processRoleCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName) throws Exception;
    
    public boolean validateRoleCmdParams(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, Subject currentUser, boolean checkPermission) throws Exception;
    
    public void processUserCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName) throws Exception;
    
    public boolean validateUserCmdParams(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, Subject currentUser, boolean checkPermission) throws Exception;
    
    public void processPermissionCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName) throws Exception;
    
    public boolean validatePermissionCmdParams(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, Subject currentUser, boolean checkPermission) throws Exception;
}
