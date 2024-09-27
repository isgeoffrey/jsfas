package jsfas.db.rbac.persistence.service;

import java.util.List;
import java.util.Optional;

import jsfas.common.json.CommonJson;

public interface PermissionService {

    public List<CommonJson> getRoleGroupPermission(Optional<String> optionalRoleGroupId, Optional<String> optionalActionType, boolean treeView);
    
    public List<CommonJson> getPermissionPredParam(String username, String entityType, String actionType);
}
