package jsfas.db.rbac.persistence.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import jsfas.common.json.CommonJson;

public class EntityRelationshipEventHandler implements EntityRelationshipService {

    @Autowired
    private PermissionService permissionService;
    
    @Override
    public List<CommonJson> getEntityRelationship(Optional<String> optionalActionType, boolean treeView) {
        // TODO Auto-generated method stub
        return permissionService.getRoleGroupPermission(Optional.empty(), optionalActionType, treeView);
    }

}
