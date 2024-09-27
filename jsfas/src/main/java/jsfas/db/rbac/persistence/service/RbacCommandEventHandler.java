package jsfas.db.rbac.persistence.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.constants.AppConstants;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.RecordExistException;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.exception.RecordRemovedException;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.UserProfileHeaderDAO;
import jsfas.db.main.persistence.repository.UserProfileHeaderRepository;
import jsfas.db.rbac.persistence.domain.ActionDAO;
import jsfas.db.rbac.persistence.domain.EntityDAO;
import jsfas.db.rbac.persistence.domain.EntityRelationshipDAO;
import jsfas.db.rbac.persistence.domain.EntityRelationshipDAOPK;
import jsfas.db.rbac.persistence.domain.PermStatusDAO;
import jsfas.db.rbac.persistence.domain.PermissionDAO;
import jsfas.db.rbac.persistence.domain.PermissionDAOPK;
import jsfas.db.rbac.persistence.domain.PredParamDAO;
import jsfas.db.rbac.persistence.domain.PredQueryParamDAO;
import jsfas.db.rbac.persistence.domain.PredicateDAO;
import jsfas.db.rbac.persistence.domain.RoleGroupDAO;
import jsfas.db.rbac.persistence.domain.UserRoleGroupDAO;
import jsfas.db.rbac.persistence.domain.UserRoleGroupDAOPK;
import jsfas.db.rbac.persistence.repository.ActionRepository;
import jsfas.db.rbac.persistence.repository.EntityRelationshipRepository;
import jsfas.db.rbac.persistence.repository.EntityRepository;
import jsfas.db.rbac.persistence.repository.PermStatusRepository;
import jsfas.db.rbac.persistence.repository.PermissionRepository;
import jsfas.db.rbac.persistence.repository.PredParamRepository;
import jsfas.db.rbac.persistence.repository.PredQueryParamRepository;
import jsfas.db.rbac.persistence.repository.RoleGroupRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;
import jsfas.security.RbacCmdType;
import jsfas.security.SecurityUtils;
import jsfas.security.realm.RbacRealm;

public class RbacCommandEventHandler implements RbacCommandService {

    @Autowired
    private RoleGroupRepository roleGroupRepository;
    
    @Autowired
    private UserRoleGroupRepository userRoleGroupRepository;
    
    @Autowired
    private UserProfileHeaderRepository userProfileHeaderRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private EntityRepository entityRepository;
    
    @Autowired
    private ActionRepository actionRepository;
    
    @Autowired
    private PermStatusRepository permStatusRepository;
    
    @Autowired
    private PredParamRepository predParamRepository;
    
    @Autowired
    private PredQueryParamRepository predQueryParamRepository;
    
    @Autowired
    private EntityRelationshipRepository entityRelationshipRepository;
    
    //@Autowired
    //private RbacCommandService rbacCommandService;
    
    @Autowired
    private ObjectFactory<RbacRealm> rbacRealmObjectFactory;
    
    @Autowired
    private Environment env;
    
    //private final String PERM_FMT_STR = "E00001:%s:FNCATA:AA02";
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processBatch() throws Exception {
        List<UserProfileHeaderDAO> userProfileHeaderList = Collections.emptyList();
        List<PermissionDAO> expirePermissionList = Collections.emptyList();
        List<PermissionDAO> allowPermissionList = Collections.emptyList();
        Optional<PermStatusDAO> optionalPermStatus = Optional.empty();
        
        log.info("processBatch start find and lock non-exist user profile headers");
        
        userProfileHeaderList = userProfileHeaderRepository.findNotInUserRoleGroupAndUserProfileDetail()
                                                           .stream()
                                                           .map(uph -> userProfileHeaderRepository.findOneForUpdate(uph.getUserName()))
                                                           .collect(Collectors.toList());
        
        log.info("processBatch end find and lock non-exist user profile headers");
        
        
        
        if(!userProfileHeaderList.isEmpty()) {
            log.info("processBatch start delete non-exist user profile headers");
            
            userProfileHeaderRepository.deleteInBatch(userProfileHeaderList);
            
            log.info(String.format("processBatch delete non-exist user profile headers total=%d", userProfileHeaderList.size()));
            log.info("processBatch end delete non-exist user profile headers");
        } else {
            log.info("processBatch no non-exist user profile headers to delete");
        }
        
        log.info("processBatch start find and lock expire permissions");
        
        expirePermissionList = permissionRepository.findExpireByPermStatusType(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_ALLOWED), null)
                                                   .stream()
                                                   .map(p -> permissionRepository.findOneForUpdate(p.getPermissionPK()))
                                                   .collect(Collectors.toList());
        
        log.info("processBatch end find and lock expire permissions");
        
        if(!expirePermissionList.isEmpty()) {
            log.info("processBatch start update expire permissions");
            
            optionalPermStatus = Optional.ofNullable(permStatusRepository.findOne(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_EXPIRED)));
            optionalPermStatus.orElseThrow(() -> new RecordRemovedException());
            
            while(expirePermissionList.iterator().hasNext()) {
                expirePermissionList.iterator().next().setPermStatus(optionalPermStatus.get());
            }
            
            permissionRepository.saveAll(expirePermissionList);
            
            log.info(String.format("processBatch update expire permissions total=%d", expirePermissionList.size()));  
            log.info("processBatch end update expire permissions");
        } else {
            log.info("processBatch no expire permissions to update");
        }
        
        log.info("processBatch start find and lock allow permissions");
        
        allowPermissionList = permissionRepository.findActiveByPermStatusType(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_INACTIVE), null)
                                                   .stream()
                                                   .map(p -> permissionRepository.findOneForUpdate(p.getPermissionPK()))
                                                   .collect(Collectors.toList());
        
        log.info("processBatch end find and lock allow permissions");
        
        if(!allowPermissionList.isEmpty()) {
            log.info("processBatch start update allow permissions");
            optionalPermStatus = Optional.ofNullable(permStatusRepository.findOne(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_ALLOWED)));
            optionalPermStatus.orElseThrow(() -> new RecordRemovedException());
            
            while(allowPermissionList.iterator().hasNext()) {
                allowPermissionList.iterator().next().setPermStatus(optionalPermStatus.get());
            }
            
            permissionRepository.saveAll(allowPermissionList);
            
            log.info(String.format("processBatch update allow permissions total=%d", allowPermissionList.size()));  
            log.info("processBatch end update allow permissions");
        } else {
            log.info("processBatch no allow permissions to update");
        }
        
        //skip clean cache if RBAC not enable in app
        if (env.getProperty(AppConstants.RBAC_PERM_ENABLED, Boolean.class, false)) {
	        if(!userProfileHeaderList.isEmpty() || !expirePermissionList.isEmpty() || !allowPermissionList.isEmpty()) {
	            log.info("processBatch start clear rbac cache");
	            rbacRealmObjectFactory.getObject().clearAllCachedAuthorizationInfo();
	            log.info("processBatch end clear rbac cache");
	        } else {
	            log.info("processBatch no need to clear rbac cache");
	        }
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processCommandList(List commandList, String opPageName) throws Exception {
        processCommandList(commandList, opPageName, true);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processCommandList(List commandList, String opPageName, boolean checkPermission) throws Exception {
        // TODO Auto-generated method stub
        ObjectMapper mapper = new ObjectMapper();
        Map<String, RoleGroupDAO> roleNodeMap = new HashMap<>();
        
        if(Optional.ofNullable(commandList).isPresent()) {
            for(Object obj: commandList) {
                try {
                    CommonJson rbacCmd = mapper.convertValue(obj, CommonJson.class);
                    String cmdType = rbacCmd.getFilterValue("cmdType").trim();
                    if(!EnumUtils.isValidEnum(RbacCmdType.class, cmdType)) {
                        log.error(String.format("Process ignore unknown rbac cmd type=%", cmdType));
                        throw new InvalidParameterException();
                    }
                    
                    Optional<Object> optionalCmdParams = Optional.ofNullable(rbacCmd.get("cmdParams", Object.class));
                    if(!optionalCmdParams.isPresent()) {
                        log.error("Process ignore rbac cmd due to missing params");
                        throw new InvalidParameterException();
                    }  
                    
                    processCommand(RbacCmdType.valueOf(cmdType.toUpperCase(Locale.ENGLISH)), 
                            mapper.convertValue(optionalCmdParams.get(), CommonJson.class), roleNodeMap, opPageName, checkPermission);
                } catch(IllegalArgumentException e) {
                    log.error(String.format("Process ignore incorrect rbac cmd format=%", obj.toString()));
                    throw new InvalidParameterException();
                }
            }
            if(!commandList.isEmpty()) {
                rbacRealmObjectFactory.getObject().clearAllCachedAuthorizationInfo();
            }
        }
    }
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName, boolean checkPermission) throws Exception {
        Subject currentUser = SecurityUtils.getSubject();
        switch(rbacCmdType) {
        case ADD_ROLE:
        case EDIT_ROLE:
        case DEL_ROLE:
            if(!validateRoleCmdParams(rbacCmdType, cmdParams, roleNodeMap, currentUser, checkPermission)) {
                log.error(String.format("Process incorrect rbac role cmd params=%s", cmdParams.toString()));
                throw new InvalidParameterException();
            }
            processRoleCommand(rbacCmdType, cmdParams, roleNodeMap, opPageName);
            break;
        case ADD_USER:
        case EDIT_USER:
        case DEL_USER:
            if(!validateUserCmdParams(rbacCmdType, cmdParams, roleNodeMap, currentUser, checkPermission)) {
                log.error(String.format("Process incorrect rbac user cmd params=%s", cmdParams.toString()));
                throw new InvalidParameterException();
            }
            processUserCommand(rbacCmdType, cmdParams, roleNodeMap, opPageName);
            break;
        case ADD_PERM:
        case EDIT_PERM:
            if(!validatePermissionCmdParams(rbacCmdType, cmdParams, roleNodeMap, currentUser, checkPermission)) {
                log.error(String.format("Process incorrect rbac permission cmd params=%s", cmdParams.toString()));
                throw new InvalidParameterException();
            }
            processPermissionCommand(rbacCmdType, cmdParams, roleNodeMap, opPageName);
            break;
        default:
            log.error(String.format("Process unknown rbac cmd type=%s", rbacCmdType.toString()));
            throw new InvalidParameterException();
        }
    }
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processRoleCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName) throws Exception {
        String parentNodeId = cmdParams.getFilterValue("parentNodeId").trim();
        String nodeId = cmdParams.getFilterValue("nodeId").trim();
        String nodeDesc = cmdParams.getFilterValue("nodeDesc");
        boolean isNew = Optional.ofNullable(cmdParams.get("isNew", Boolean.class)).orElse(false);
        String modCtrlTxt = cmdParams.getFilterValue("modCtrlTxt").trim();
        String newModCtrlTxt = GeneralUtil.genModCtrlTxt();
        Optional<RoleGroupDAO> optionalParentRoleGroup = Optional.empty();
        Optional<RoleGroupDAO> optionalRoleGroup = Optional.empty();
        String remoteUser = SecurityUtils.getCurrentLogin();
        Timestamp today = GeneralUtil.getCurrentTimestamp();
        
        //New parent role node is created with 'N' prefix as node id
        if(!GeneralUtil.isInteger(parentNodeId)) {
            optionalParentRoleGroup = Optional.ofNullable(roleNodeMap.get(parentNodeId));
        } else if(!parentNodeId.isEmpty()){
            optionalParentRoleGroup = Optional.ofNullable(roleGroupRepository.findOneForUpdate(parentNodeId)); 
            optionalParentRoleGroup.orElseThrow(() -> new RecordRemovedException());
        }
        
        if(!isNew) {
            optionalRoleGroup = Optional.ofNullable(roleGroupRepository.findOneForUpdate(nodeId));
            optionalRoleGroup.orElseThrow(() -> new RecordRemovedException());
            if(optionalRoleGroup.isPresent() && !optionalRoleGroup.get().getModCtrlTxt().contentEquals(modCtrlTxt)) {
                throw new RecordModifiedException();
            }
        }
        
        switch(rbacCmdType) {
        case ADD_ROLE:
        case EDIT_ROLE:
            RoleGroupDAO roleGroup = optionalRoleGroup.orElse(new RoleGroupDAO());
            if(optionalParentRoleGroup.isPresent()) {
                roleGroup.setParentRoleGroup(optionalParentRoleGroup.get());
            }
            roleGroup.setRoleGroupDesc(nodeDesc);
            roleGroup.setModCtrlTxt(newModCtrlTxt);
            if(!Optional.ofNullable(roleGroup.getCreateDate()).isPresent()) {
                roleGroup.setCreateDate(today);
            }
            if(!Optional.ofNullable(roleGroup.getCreateUser()).isPresent()) {
                roleGroup.setCreateUser(remoteUser);
            }
            roleGroup.setChangeDate(today);
            roleGroup.setChangeUser(remoteUser);
            roleGroup.setOpPageName(remoteUser);
            roleGroup = roleGroupRepository.save(roleGroup);
            if(isNew) {
                roleNodeMap.put(nodeId, roleGroup);
            }
            break;
        case DEL_ROLE:
            roleGroupRepository.delete(optionalRoleGroup.get());
            break;
        default:
            log.error(String.format("Process unknown rbac role cmd type=%", rbacCmdType));
            throw new InvalidParameterException();
        }
    }
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", propagation = Propagation.SUPPORTS)
    public boolean validateRoleCmdParams(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, Subject currentUser, boolean checkPermission) throws Exception {
        boolean validate = false;
        String parentNodeId = cmdParams.getFilterValue("parentNodeId").trim();
        String parentNodeIdOld = cmdParams.getFilterValue("parentNodeIdOld").trim();
        String nodeId = cmdParams.getFilterValue("nodeId").trim();
        String nodeDesc = cmdParams.getFilterValue("nodeDesc").trim();
        boolean isNew = Optional.ofNullable(cmdParams.get("isNew", Boolean.class)).orElse(false);
        String nodeType = cmdParams.getFilterValue("nodeType").trim();
        String modCtrlTxt = cmdParams.getFilterValue("modCtrlTxt").trim();
        
        if(!GeneralUtil.isInteger(parentNodeId)) {
            if(!roleNodeMap.containsKey(parentNodeId)) {
                parentNodeId = StringUtils.EMPTY;
            }
        }
        
        switch(rbacCmdType) {
        case ADD_ROLE:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.ADD_ACTION_TYPE));
            }
            if(!parentNodeId.isEmpty() 
                    && !parentNodeIdOld.isEmpty()
                    && parentNodeId.equals(parentNodeIdOld)
                    && isNew
                    && !nodeId.isEmpty()
                    && !nodeDesc.isEmpty()
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_ROLE_NODE_TYPE))
                    && modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        case EDIT_ROLE:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.CHG_ACTION_TYPE));
            }
            if(!isNew 
                    && !nodeId.isEmpty()
                    && !nodeDesc.isEmpty() 
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_ROLE_NODE_TYPE))
                    && !modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        case DEL_ROLE:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.REM_ACTION_TYPE));
            }
            if(!isNew 
                    && !nodeId.isEmpty()
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_ROLE_NODE_TYPE))
                    && !modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        default:
            log.error(String.format("Validate unknown rbac role cmd type=%", rbacCmdType));
            throw new InvalidParameterException();
        }
        return validate;
    }
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processUserCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName) throws Exception {
        String parentNodeId = cmdParams.getFilterValue("parentNodeId").trim();
        String parentNodeIdOld = cmdParams.getFilterValue("parentNodeIdOld").trim();
        String nodeId = cmdParams.getFilterValue("nodeId").trim();
        String nodeDesc = cmdParams.getFilterValue("nodeDesc").trim();
        boolean isNew = Optional.ofNullable(cmdParams.get("isNew", Boolean.class)).orElse(false);
        String modCtrlTxt = cmdParams.getFilterValue("modCtrlTxt").trim();
        String newModCtrlTxt = GeneralUtil.genModCtrlTxt();
        Optional<UserRoleGroupDAO> optionalUserRoleGroup = Optional.empty();
        Optional<RoleGroupDAO> optionalRoleGroup = Optional.empty();
        Optional<UserProfileHeaderDAO> optionalUserProfileHeader = Optional.empty();
        String remoteUser = SecurityUtils.getCurrentLogin();
        Timestamp today = GeneralUtil.getCurrentTimestamp();
        
        //New role node is created with 'N' prefix as node id
        if(!GeneralUtil.isInteger(parentNodeIdOld)) {
            optionalRoleGroup = Optional.ofNullable(roleNodeMap.get(parentNodeIdOld));
        } else {
            optionalRoleGroup = Optional.ofNullable(roleGroupRepository.findOneForUpdate(parentNodeIdOld));
        }
        
        optionalRoleGroup.orElseThrow(() -> new RecordRemovedException());
        
        if(!isNew) {
            optionalUserProfileHeader = Optional.ofNullable(userProfileHeaderRepository.findOneForUpdate(nodeId));
            optionalUserProfileHeader.orElseThrow(() -> new RecordRemovedException());
            optionalUserRoleGroup = Optional.ofNullable(userRoleGroupRepository.findOneForUpdate(new UserRoleGroupDAOPK(optionalUserProfileHeader.get(), optionalRoleGroup.get())));
            optionalUserRoleGroup.orElseThrow(() -> new RecordRemovedException());
            if(optionalUserRoleGroup.isPresent() && !optionalUserRoleGroup.get().getModCtrlTxt().contentEquals(modCtrlTxt)) {
                throw new RecordModifiedException();
            }
        } else {
            optionalUserProfileHeader = Optional.ofNullable(userProfileHeaderRepository.findOneForUpdate(nodeDesc));
            optionalUserProfileHeader = Optional.ofNullable(optionalUserProfileHeader.orElseGet(() -> {
                UserProfileHeaderDAO userProfileHeader = new UserProfileHeaderDAO();
                userProfileHeader.setUserName(nodeDesc);
                userProfileHeader.setDefaultPrintQueue(GeneralUtil.initBlankString(StringUtils.EMPTY));
                userProfileHeader.setModCtrlTxt(newModCtrlTxt);
                userProfileHeader.setCreateUser(remoteUser);
                userProfileHeader.setCreateDate(today);
                userProfileHeader.setChangeUser(remoteUser);
                userProfileHeader.setChangeDate(today);
                userProfileHeader.setOpPageName(opPageName);
                return userProfileHeader;
            }));
            if(userRoleGroupRepository.existsById(new UserRoleGroupDAOPK(optionalUserProfileHeader.get(), optionalRoleGroup.get()))) {
                throw new RecordExistException();
            }
        }
        
        switch(rbacCmdType) {
        case ADD_USER:
        case EDIT_USER:
            if(!isNew) {
                optionalUserProfileHeader.get().getUserRoleGroups().remove(optionalUserRoleGroup.get());
                userRoleGroupRepository.delete(optionalUserRoleGroup.get());
                //New role node is created with 'N' prefix as node id
                if(!GeneralUtil.isInteger(parentNodeId)) {
                    optionalRoleGroup = Optional.ofNullable(roleNodeMap.get(parentNodeId));
                } else {
                    optionalRoleGroup = Optional.ofNullable(roleGroupRepository.findOneForUpdate(parentNodeId));
                }
                
                optionalRoleGroup.orElseThrow(() -> new RecordRemovedException());
            }
            
            UserRoleGroupDAO userRoleGroup = new UserRoleGroupDAO(new UserRoleGroupDAOPK(optionalUserProfileHeader.get(), optionalRoleGroup.get()));
            UserRoleGroupDAOPK userRoleGroupPK = userRoleGroup.getUserRoleGroupPK();
            UserProfileHeaderDAO userProfileHeader = userRoleGroupPK.getUserProfileHeader();
            Set<UserRoleGroupDAO> userRoleGroupSet = userProfileHeader.getUserRoleGroups();
            userRoleGroupSet.add(userRoleGroup);
            userProfileHeader.setUserRoleGroups(userRoleGroupSet);
            userRoleGroupPK.setUserProfileHeader(userProfileHeader);
            userRoleGroup.setUserRoleGroupPK(userRoleGroupPK);
            userRoleGroup.setModCtrlTxt(newModCtrlTxt);
            if(!Optional.ofNullable(userRoleGroup.getCreateDate()).isPresent()) {
                userRoleGroup.setCreateDate(today);
            }
            if(!Optional.ofNullable(userRoleGroup.getCreateUser()).isPresent()) {
                userRoleGroup.setCreateUser(remoteUser);
            }
            userRoleGroup.setChangeDate(today);
            userRoleGroup.setChangeUser(remoteUser);
            userRoleGroup.setOpPageName(opPageName);
            userProfileHeaderRepository.save(userProfileHeader);
            break;
        case DEL_USER:
            userRoleGroupRepository.delete(optionalUserRoleGroup.get());
            break;
        default:
            log.error(String.format("Process unknown rbac user cmd type=%", rbacCmdType));
            throw new InvalidParameterException();
        }
    }
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", propagation = Propagation.SUPPORTS)
    public boolean validateUserCmdParams(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, Subject currentUser, boolean checkPermission) throws Exception {
        boolean validate = false;
        String parentNodeId = cmdParams.getFilterValue("parentNodeId").trim();
        String parentNodeIdOld = cmdParams.getFilterValue("parentNodeIdOld").trim();
        String nodeId = cmdParams.getFilterValue("nodeId").trim();
        String nodeDesc = cmdParams.getFilterValue("nodeDesc").trim();
        boolean isNew = Optional.ofNullable(cmdParams.get("isNew", Boolean.class)).orElse(false);
        String nodeType = cmdParams.getFilterValue("nodeType").trim();
        String modCtrlTxt = cmdParams.getFilterValue("modCtrlTxt").trim();
        
        if(!GeneralUtil.isInteger(parentNodeId)) {
            if(!roleNodeMap.containsKey(parentNodeId)) {
                parentNodeId = StringUtils.EMPTY;
            }
        }
        
        switch(rbacCmdType) {
        case ADD_USER:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.ADD_ACTION_TYPE));
            }
            if(!parentNodeId.isEmpty()
                    && !parentNodeIdOld.isEmpty()
                    && parentNodeId.equals(parentNodeIdOld)
                    && isNew
                    && !nodeId.isEmpty() 
                    && !nodeDesc.isEmpty()
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_USER_NODE_TYPE))
                    && modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        case EDIT_USER:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.CHG_ACTION_TYPE));
            }
            if(!parentNodeId.isEmpty() 
                    && !parentNodeIdOld.isEmpty()
                    && !parentNodeId.equals(parentNodeIdOld)
                    && !isNew
                    && !nodeId.isEmpty() 
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_USER_NODE_TYPE))
                    && !modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        case DEL_USER:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.REM_ACTION_TYPE));
            }
            if(!parentNodeId.isEmpty() 
                    && !parentNodeIdOld.isEmpty()
                    && parentNodeId.equals(parentNodeIdOld)
                    && !isNew
                    && !nodeId.isEmpty() 
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_USER_NODE_TYPE))
                    && !modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        default:
            log.error(String.format("Validate unknown rbac user cmd type=%", rbacCmdType));
            throw new InvalidParameterException();
        }
        
        return validate;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
    public void processPermissionCommand(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, String opPageName) throws Exception {
        String roleGroupId = cmdParams.getFilterValue("roleGroupId").trim();
        String parentNodeId = cmdParams.getFilterValue("parentNodeId").trim();
        String nodeId = cmdParams.getFilterValue("nodeId").trim();
        String permEffectiveDateStr = cmdParams.getFilterValue("permEffectiveDate").trim();
        String permExpiryDateStr = cmdParams.getFilterValue("permExpiryDate").trim();
        Timestamp today = GeneralUtil.truncateDate(GeneralUtil.getCurrentTimestamp());
        Timestamp permEffectiveDate = GeneralUtil.initNullTimestamp(GeneralUtil.convertStringToTimestamp(permEffectiveDateStr));
        Timestamp permExpiryDate = GeneralUtil.initNullTimestamp(GeneralUtil.convertStringToTimestamp(permExpiryDateStr));
        boolean checked = Optional.ofNullable(cmdParams.get("checked", Boolean.class)).orElse(false);
        String modCtrlTxt = cmdParams.getFilterValue("modCtrlTxt").trim();
        String newModCtrlTxt = GeneralUtil.genModCtrlTxt();
        Optional<RoleGroupDAO> optionalRoleGroup = Optional.empty();
        Optional<EntityDAO> optionalEntity = Optional.empty();
        Optional<ActionDAO> optionalAction = Optional.empty();
        Optional<PermissionDAO> optionalPermission = Optional.empty();
        Optional<PermStatusDAO> optionalPermStatus = Optional.empty();
        Optional<Map> optionalPermPredParamMap = Optional.ofNullable(cmdParams.get("permPredParam", Map.class));
        Optional<CommonJson> optionalPermPredParam = Optional.empty();
        Optional<PredParamDAO> optionalPredParam = Optional.empty();
        Optional<PredQueryParamDAO> optionalPredQueryParam = Optional.empty();
        Optional<PredicateDAO> optionalPredicate = Optional.empty();
        Optional<EntityRelationshipDAO> optionalEntityRelationship = Optional.empty();
        String remoteUser = SecurityUtils.getCurrentLogin();
        String predParamType = StringUtils.EMPTY;
        String predQueryParamType = StringUtils.EMPTY;
        String predParamDesc = StringUtils.EMPTY;
        String predParamModCtrlTxt = StringUtils.EMPTY;
        
        if(optionalPermPredParamMap.isPresent()) {
            optionalPermPredParam = Optional.ofNullable(new ObjectMapper().convertValue(optionalPermPredParamMap.get(), CommonJson.class));
        }
        
        //New role node is created with 'N' prefix as node id
        if(!GeneralUtil.isInteger(roleGroupId)) {
            optionalRoleGroup = Optional.ofNullable(roleNodeMap.get(roleGroupId));
        } else {
            optionalRoleGroup = Optional.ofNullable(roleGroupRepository.findOneForUpdate(roleGroupId));
        }
        
        optionalRoleGroup.orElseThrow(() -> new RecordRemovedException());
        
        optionalEntity = Optional.ofNullable(entityRepository.findOne(parentNodeId));
        optionalEntity.orElseThrow(() -> new RecordRemovedException());
        
        optionalAction = Optional.ofNullable(actionRepository.findOne(nodeId));
        optionalAction.orElseThrow(() -> new RecordRemovedException());
        
        if(!modCtrlTxt.isEmpty()) {
            optionalPermission = Optional.ofNullable(permissionRepository.findOneForUpdate(new PermissionDAOPK(optionalRoleGroup.get(), optionalEntity.get(), optionalAction.get())));
            optionalPermission.orElseThrow(() -> new RecordRemovedException());
            if(optionalPermission.isPresent() && !optionalPermission.get().getModCtrlTxt().contentEquals(modCtrlTxt)) {
                throw new RecordModifiedException();
            }
        }
        
        if(checked && (GeneralUtil.isBlankTimestamp(permEffectiveDate) 
                || GeneralUtil.isSameDay(today, permEffectiveDate)
                || GeneralUtil.isSameDay(today, permExpiryDate)
                || (today.after(permEffectiveDate) && permExpiryDate.after(today)))) {
            optionalPermStatus = Optional.ofNullable(permStatusRepository.findOne(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_ALLOWED)));
        } else if(checked && permEffectiveDate.after(today)) {
            optionalPermStatus = Optional.ofNullable(permStatusRepository.findOne(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_INACTIVE)));
        } else if(checked && today.after(permExpiryDate)) {
            optionalPermStatus = Optional.ofNullable(permStatusRepository.findOne(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_EXPIRED)));
        } else {
            optionalPermStatus = Optional.ofNullable(permStatusRepository.findOne(env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_DEFAULT)));
        }
        
        optionalPermStatus.orElseThrow(() -> new RecordRemovedException());
        
        switch(rbacCmdType) {
        case ADD_PERM:
        case EDIT_PERM:
            PermissionDAO permission = optionalPermission.orElse(new PermissionDAO(new PermissionDAOPK(optionalRoleGroup.get(), optionalEntity.get(), optionalAction.get())));
            permission.setEffectiveDate(permEffectiveDate);
            permission.setExpiryDate(permExpiryDate);
            permission.setPermStatus(optionalPermStatus.get());
            permission.setModCtrlTxt(newModCtrlTxt);
            if(!Optional.ofNullable(permission.getCreateDate()).isPresent()) {
                permission.setCreateDate(today);
            }
            if(!Optional.ofNullable(permission.getCreateUser()).isPresent()) {
                permission.setCreateUser(remoteUser);
            }
            permission.setChangeDate(today);
            permission.setChangeUser(remoteUser);
            permission.setOpPageName(opPageName);
            if(optionalPermPredParam.isPresent()) {
                predParamType = optionalPermPredParam.get().getFilterValue("predParamType").trim();
                predQueryParamType = optionalPermPredParam.get().getFilterValue("predQueryParamType").trim();
                predParamDesc = optionalPermPredParam.get().getFilterValue("predParamDesc");
                predParamModCtrlTxt = optionalPermPredParam.get().getFilterValue("modCtrlTxt").trim();
                if(!predParamType.isEmpty()) {
                    optionalPredParam = Optional.ofNullable(predParamRepository.findOneForUpdate(predParamType));
                }
                optionalPredQueryParam = predQueryParamRepository.findById(predQueryParamType);
                optionalPredQueryParam.orElseThrow(() -> new RecordRemovedException());
                optionalEntityRelationship = Optional.ofNullable(entityRelationshipRepository.findOne(new EntityRelationshipDAOPK(optionalEntity.get(), optionalAction.get())));
                optionalEntityRelationship.orElseThrow(() -> new RecordRemovedException());
                optionalPredicate = optionalEntityRelationship.get().getPredicate();
                optionalPredicate.orElseThrow(() -> new RecordRemovedException());
                if(!predParamModCtrlTxt.isEmpty() 
                        && optionalPredParam.isPresent() 
                        && !optionalPredParam.get().getModCtrlTxt().contentEquals(predParamModCtrlTxt)) {
                    throw new RecordModifiedException();
                }
                PredParamDAO predParam = optionalPredParam.orElse(new PredParamDAO());  
                predParam.setPredQueryParamStr(optionalPermPredParam.get().getFilterValue("predQueryParamStr"));
                predParam.setPredQueryParam(optionalPredQueryParam.get());
                predParam.setPredParamDesc(predParamDesc);
                predParam.setPredicate(optionalPredicate.get());
                predParam.getPermissions().add(permission);
                predParam.setModCtrlTxt(newModCtrlTxt);
                if(!Optional.ofNullable(predParam.getCreateDate()).isPresent()) {
                    predParam.setCreateDate(today);
                }
                if(!Optional.ofNullable(predParam.getCreateUser()).isPresent()) {
                    predParam.setCreateUser(remoteUser);
                }
                predParam.setChangeDate(today);
                predParam.setChangeUser(remoteUser);
                predParam.setOpPageName(opPageName);
                predParam = predParamRepository.save(predParam);
                permission.setPredParam(predParam);
            } else {
                permissionRepository.save(permission);
            }
            break;
        default:
            log.error(String.format("Process unknown rbac perm cmd type=%", rbacCmdType));
            throw new InvalidParameterException();
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(value = "transactionManagerJsfasMain", propagation = Propagation.SUPPORTS)
    public boolean validatePermissionCmdParams(RbacCmdType rbacCmdType, CommonJson cmdParams, Map<String, RoleGroupDAO> roleNodeMap, Subject currentUser, boolean checkPermission) throws Exception {
        boolean validate = false;
        String roleGroupId = cmdParams.getFilterValue("roleGroupId").trim();
        String parentNodeId = cmdParams.getFilterValue("parentNodeId").trim();
        String nodeId = cmdParams.getFilterValue("nodeId").trim();
        String permEffectiveDateStr = cmdParams.getFilterValue("permEffectiveDate").trim();
        String permExpiryDateStr = cmdParams.getFilterValue("permExpiryDate").trim();
        Timestamp today = GeneralUtil.truncateDate(GeneralUtil.getCurrentTimestamp());
        Timestamp permEffectiveDate = today;
        Timestamp permExpiryDate = today;
        String nodeType = cmdParams.getFilterValue("nodeType").trim();
        Optional<Map> optionalPermPredParamMap = Optional.ofNullable(cmdParams.get("permPredParam", Map.class));
        Optional<CommonJson> optionalPermPredParam = Optional.empty();
        Optional<String> optionalPermPredParamModCtrlTxt = Optional.empty();
        String predQueryParamType = StringUtils.EMPTY;
        String predParamDesc = StringUtils.EMPTY;
        String modCtrlTxt = cmdParams.getFilterValue("modCtrlTxt").trim();
        
        if(optionalPermPredParamMap.isPresent()) {
            optionalPermPredParam = Optional.ofNullable(new ObjectMapper().convertValue(optionalPermPredParamMap.get(), CommonJson.class));
        }
        
        if(optionalPermPredParam.isPresent()) {
            optionalPermPredParamModCtrlTxt = Optional.ofNullable(optionalPermPredParam.get().get("modCtrlTxt"));
            predQueryParamType = optionalPermPredParam.get().getFilterValue("predQueryParamType").trim();
            predParamDesc = optionalPermPredParam.get().getFilterValue("predParamDesc").trim();
            if(predQueryParamType.isEmpty()) {
                log.error("Missing pred query param type");
                throw new InvalidParameterException();
            }
            if(predParamDesc.isEmpty()) {
                log.error("Missing pred param desc");
                throw new InvalidParameterException();
            }
        }
        
        if(!GeneralUtil.isInteger(roleGroupId)) {
            if(!roleNodeMap.containsKey(roleGroupId)) {
                roleGroupId = StringUtils.EMPTY;
            }
        }
        if(GeneralUtil.isDate(permEffectiveDateStr)) {
            permEffectiveDate = GeneralUtil.convertStringToTimestamp(permEffectiveDateStr);
        }
        if(GeneralUtil.isDate(permExpiryDateStr)) {
            permExpiryDate = GeneralUtil.convertStringToTimestamp(permExpiryDateStr);
        }
        
        switch(rbacCmdType) {
        case ADD_PERM:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.ADD_ACTION_TYPE));
            }
            if(!roleGroupId.isEmpty() 
                    && !parentNodeId.isEmpty() 
                    && !nodeId.isEmpty()
                    && ((!permEffectiveDateStr.isEmpty() && !permExpiryDateStr.isEmpty()) 
                            || (permEffectiveDateStr.isEmpty() && permExpiryDateStr.isEmpty()))
                    /*
                    && (GeneralUtil.isSameDay(today, permEffectiveDate) 
                            || GeneralUtil.dateRangeOk(GeneralUtil.getStringByDate(today), GeneralUtil.getStringByDate(permEffectiveDate)))
                    */
                    && (GeneralUtil.isSameDay(permEffectiveDate, permExpiryDate) 
                            || GeneralUtil.dateRangeOk(GeneralUtil.getStringByDate(permEffectiveDate), GeneralUtil.getStringByDate(permExpiryDate)))
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_ACTION_NODE_TYPE))
                    && modCtrlTxt.isEmpty()
                    && !optionalPermPredParamModCtrlTxt.isPresent()) {
                validate = true;
            }
            break;
        case EDIT_PERM:
            if(checkPermission) {
                currentUser.checkPermission(String.format(env.getProperty(AppConstants.RBAC_PERM_FMT_STR), AppConstants.CHG_ACTION_TYPE));  
            }
            if(!roleGroupId.isEmpty() 
                    && !parentNodeId.isEmpty() 
                    && !nodeId.isEmpty()
                    && ((!permEffectiveDateStr.isEmpty() && !permExpiryDateStr.isEmpty()) 
                            || (permEffectiveDateStr.isEmpty() && permExpiryDateStr.isEmpty()))
                    /*
                    && (GeneralUtil.isSameDay(today, permEffectiveDate) 
                            || GeneralUtil.dateRangeOk(GeneralUtil.getStringByDate(today), GeneralUtil.getStringByDate(permEffectiveDate)))
                    */
                    && (GeneralUtil.isSameDay(permEffectiveDate, permExpiryDate) 
                            || GeneralUtil.dateRangeOk(GeneralUtil.getStringByDate(permEffectiveDate), GeneralUtil.getStringByDate(permExpiryDate)))
                    && nodeType.contentEquals(env.getRequiredProperty(AppConstants.RBAC_ACTION_NODE_TYPE))
                    && !modCtrlTxt.isEmpty()) {
                validate = true;
            }
            break;
        default:
            log.error(String.format("Validate unknown rbac perm cmd type=%", rbacCmdType));
            throw new InvalidParameterException();
        }
        
        return validate;
    }

}
