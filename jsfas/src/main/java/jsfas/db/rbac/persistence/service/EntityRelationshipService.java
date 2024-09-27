package jsfas.db.rbac.persistence.service;

import java.util.List;
import java.util.Optional;

import jsfas.common.json.CommonJson;

public interface EntityRelationshipService {

    public List<CommonJson> getEntityRelationship(Optional<String> optionalActionType, boolean treeView);
}
