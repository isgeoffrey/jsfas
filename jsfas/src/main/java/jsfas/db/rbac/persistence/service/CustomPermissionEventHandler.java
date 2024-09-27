package jsfas.db.rbac.persistence.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.constants.AppConstants;
import jsfas.common.json.CommonJson;
import jsfas.security.SecurityUtils;


public class CustomPermissionEventHandler implements CustomPermissionService {

    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private Environment env;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private final int TOKEN_LEN = 2;
    
    private final int TOKEN_ENTITY_INDEX = 0;
    
    private final int TOKEN_ACTION_INDEX = 1;
    
    private Function<CommonJson, Stream<String>> flatMapToDeptSet = commonJson -> {
        Set<String> deptSet = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        String predQueryParamStr = commonJson.get("predQueryParamStr");
        try {
            CommonJson predQueryParamJson = mapper.readValue(predQueryParamStr, CommonJson.class);
            if(predQueryParamJson.getProps().containsKey("deptList")) {
                deptSet.addAll(Stream.of(predQueryParamJson.get("deptList", List.class).toArray()).map(Object::toString).collect(Collectors.toSet()));
            }
        } catch(IOException e) {
            log.error(String.format("Cannot convert json string=%s", predQueryParamStr), e);
            throw new RuntimeException(e);
        }
        return deptSet.stream();
    };
    
    @Override
    public boolean checkDepartment(String entityAction, Set<String> deptSet) {
        // TODO Auto-generated method stub
        if(!Optional.ofNullable(entityAction).isPresent() || entityAction.isEmpty()) {
            log.error("Empty entityAction string");
            return false;
        }
        
        List<String> entityActionList = Arrays.asList(entityAction.split(":"));
        
        if(entityActionList.size() != TOKEN_LEN) {
            log.error(String.format("Invalid entityAction string=%s, token length=%d", entityAction, entityActionList.size()));
            return false;
        }
        
        String entityType = entityActionList.get(TOKEN_ENTITY_INDEX);
        String actionType = entityActionList.get(TOKEN_ACTION_INDEX);
        
        if(!SecurityUtils.getSubject().hasRole(env.getRequiredProperty(AppConstants.RBAC_ROLE_SYSADMIN)) 
                && !permissionService.getPermissionPredParam(SecurityUtils.getCurrentLogin(), entityType, actionType)
                                     .stream()
                                     .filter(o -> o.get("predQueryParamType").equals(AppConstants.PRED_QUERY_PARAM_JSON))
                                     .flatMap(flatMapToDeptSet)
                                     .collect(Collectors.toSet()).containsAll(deptSet)) {
            return false;
        }
        
        return true;
    }

}
