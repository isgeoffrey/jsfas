package jsfas.db.rbac.persistence.service;

import java.util.List;
import java.util.Optional;

import jsfas.common.json.CommonJson;

public interface UserRoleGroupService {
    
    public List<CommonJson> getUserRoleGroup(Optional<String> optionalUserName, boolean treeView);
}
