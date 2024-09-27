package jsfas.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jsfas.common.annotation.Function;
import jsfas.common.constants.AppConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.CommonResponseJson;
import jsfas.common.json.Response;
import jsfas.common.json.ResponseJson;
import jsfas.db.main.persistence.service.UserProfileService;
import jsfas.db.rbac.persistence.service.EntityRelationshipService;
import jsfas.db.rbac.persistence.service.PermissionService;
import jsfas.db.rbac.persistence.service.RbacCommandService;
import jsfas.db.rbac.persistence.service.UserRoleGroupService;
import jsfas.security.SecurityUtils;

@RestController
@ControllerAdvice
public class RbacApiController extends CommonApiController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private EntityRelationshipService entityRelationshipService;
    
    @Autowired
    private UserRoleGroupService userRoleGroupService;
    
    @Autowired
    private RbacCommandService rbacCommandService;

    @Autowired
    private UserProfileService userProfileService;
    
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.VW_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.GET_RBAC_USER_INFO, method = RequestMethod.POST)
    public Response getRbacUserInfo(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {
            @NotEmpty
            private String userName = inputJson.get("userName");
            
            @NotEmpty
            private String roleGroupId = inputJson.get("roleGroupId");
        }, result);
        
        String userName = inputJson.getFilterValue("userName").trim();
        String roleGroupId = inputJson.getFilterValue("roleGroupId").trim();
        Response response = new Response();
        CommonJson commonJson = new CommonJson();
        
        response.setCode(Integer. parseInt(AppConstants.RESPONSE_JSON_SUCCESS_CODE));
        response.setData(commonJson.set( "role_user_info" ,userProfileService.getUserProfile(userName)));
        
        return setSuccess(response);
    }
    
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.VW_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.GET_RBAC_ROLE_GRP_PERM, method = RequestMethod.POST)
    public Response getRbacRoleGroupPermission(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {
            @NotEmpty
            private String roleGroupId = inputJson.get("roleGroupId");
        }, result);
        
        String actionType = inputJson.getFilterValue("actionType").trim();
        Optional<Boolean> treeView = Optional.ofNullable(inputJson.get("treeView", Boolean.class));
        Response response = new Response();
        CommonJson commonJson = new CommonJson();
        
        response.setCode(Integer. parseInt(AppConstants.RESPONSE_JSON_SUCCESS_CODE));
        response.setData(commonJson.set( "role_group_prm" ,permissionService.getRoleGroupPermission(Optional.ofNullable(inputJson.getFilterValue("roleGroupId").trim()), Optional.ofNullable(actionType.length() == 0? null: actionType), 
                treeView.orElse(false)))); 
        
        
        return setSuccess(response);
    }
    
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.VW_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.GET_RBAC_ENTITY_REL, method = RequestMethod.POST)
    public Response getRbacEntityRelationship(@RequestBody CommonJson inputJson) {
        String actionType = inputJson.getFilterValue("actionType").trim();
        Optional<Boolean> treeView = Optional.ofNullable(inputJson.get("treeView", Boolean.class));
        Response response = new Response();
        CommonJson commonJson = new CommonJson();
        
        response.setCode(Integer. parseInt(AppConstants.RESPONSE_JSON_SUCCESS_CODE));
        response.setData(commonJson.set( "new_role_group_prm" ,entityRelationshipService.getEntityRelationship(Optional.ofNullable(actionType.length() == 0? null: actionType), 
                treeView.orElse(false))));
        return response;
    }
    
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.VW_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.GET_RBAC_USER_ROLE_GRP, method = RequestMethod.POST)
    public Response getRbacUserRoleGroup(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {

        validate(new Object() {
            @NotEmpty
            private String userName = inputJson.get("userName");
        }, result);
        
        Optional<Boolean> treeView = Optional.ofNullable(inputJson.get("treeView", Boolean.class));
        Response response = new Response();
        CommonJson commonJson = new CommonJson();
        
        response.setCode(Integer. parseInt(AppConstants.RESPONSE_JSON_SUCCESS_CODE));
        response.setData(commonJson.set( "rbac_user_role_group" ,userRoleGroupService.getUserRoleGroup(Optional.ofNullable(inputJson.getFilterValue("userName").trim()), 
                treeView.orElse(false)))); 
        
        return setSuccess(response);
    }
    
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.VW_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.GET_RBAC_USERS_BY_PARAM_FUZZY, method = RequestMethod.POST)
    public CommonResponseJson getRbacUsersByParamFuzzy(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {
            @NotEmpty
            private String key = inputJson.get("key");
            
            @NotEmpty
            private String value = inputJson.get("value");
        }, result);
        
        CommonResponseJson responseJson = new CommonResponseJson();
        responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        List<CommonJson> results = new ArrayList<>();
        responseJson.setCommonJsonList(results);
        
        return responseJson;
    }
    
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.VW_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.CHK_RBAC_USER_NAME, method = RequestMethod.POST)
    public ResponseJson checkRbacUsername(@RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        
        validate(new Object() {
            @NotEmpty
            private String userName = inputJson.get("userName");
        }, result);
        
        ResponseJson responseJson = new ResponseJson();
        responseJson.setStatus(AppConstants.RESPONSE_JSON_SUCCESS_CODE);
        
        return responseJson;
    }
    
    @SuppressWarnings("rawtypes")
    @Function(functionCode = AppConstants.RBAC_MAINT_FUNC_CDE, functionSubCode = AppConstants.ADD_FUNC_SUB_CDE)
    @RequestMapping(value = RestURIConstants.EXE_RBAC_CMD_LIST, method = RequestMethod.POST)
    public Response executeRbacCommandList(HttpServletRequest request, @RequestBody CommonJson inputJson, BindingResult result) throws Exception {
        validate(new Object() {
            @NotEmpty
            private List commandList = inputJson.get("cmdList", List.class);
        }, result);
        
        Response response = new Response();
        CommonJson commonJson = new CommonJson();
        
        response.setCode(Integer. parseInt(AppConstants.RESPONSE_JSON_SUCCESS_CODE));;
        rbacCommandService.processCommandList(inputJson.get("cmdList", List.class), getOpPageName(request));
        
        return setSuccess(response);
    }
    
}
