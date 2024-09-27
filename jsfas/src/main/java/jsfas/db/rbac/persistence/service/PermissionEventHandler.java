package jsfas.db.rbac.persistence.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;

import jsfas.common.constants.AppConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.rbac.persistence.domain.EntityDAO;
import jsfas.db.rbac.persistence.domain.EntityRelationshipDAO;
import jsfas.db.rbac.persistence.domain.PermStatusDAO;
import jsfas.db.rbac.persistence.domain.PermissionDAO;
import jsfas.db.rbac.persistence.domain.PredParamDAO;
import jsfas.db.rbac.persistence.repository.EntityRelationshipRepository;
import jsfas.db.rbac.persistence.repository.EntityRepository;
import jsfas.db.rbac.persistence.repository.PermStatusRepository;
import jsfas.db.rbac.persistence.repository.PermissionRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;

public class PermissionEventHandler implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private PermStatusRepository permStatusRepository;

    @Autowired
    private EntityRepository entityRepository;
    
    @Autowired
    private EntityRelationshipRepository entityRelationshipRepository;
    
    @Autowired
    private UserRoleGroupRepository userRoleGroupRepository;
    
    @Autowired
    private Environment env;
    
    private Sort sortEntityByEntityType = new Sort("entityType");
    
    private Sort sortEntityRelationshipByActionType = new Sort("entityRelationshipPK.action.actionType");
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private Function<EntityDAO, CommonJson> mapToEntityNode = entity -> {
        CommonJson commonJson = new CommonJson();
        Optional<EntityDAO> optionalParentEntity = entityRepository.findParentEntityByEntityType(entity.getEntityType());
        commonJson.set("nodeId", entity.getEntityType());
        commonJson.set("nodeDesc", entity.getEntityDesc());
        commonJson.set("nodeType", entity.getEntityCat().getEntityCatType());
        if(optionalParentEntity.isPresent()) {
            commonJson.set("parentNodeId", optionalParentEntity.get().getEntityType());
            commonJson.set("isRoot", "false");
        } else {
            commonJson.set("parentNodeId", null);
            commonJson.set("isRoot", "true");
        }
        log.debug(String.format("Create node=%s", commonJson.toString()));
        return commonJson;
    };
    
    private Function<PredParamDAO, CommonJson> mapToPermPredParam = predParam -> {
        CommonJson commonJson = new CommonJson();
        commonJson.set("predParamType", predParam.getPredParamType());
        commonJson.set("predQueryParamStr", predParam.getPredQueryParamStr());
        commonJson.set("oldPredQueryParamStr", commonJson.get("predQueryParamStr"));
        commonJson.set("predQueryParamType", predParam.getPredQueryParam().getPredQueryParamType());
        commonJson.set("modCtrlTxt", predParam.getModCtrlTxt());
        return commonJson;
    };
    
    private Function<PredParamDAO, CommonJson> mapToPermPredParamDef = predParam -> {
        CommonJson commonJson = mapToPermPredParam.apply(predParam);
        //Override default predParam's predParamType & modCtrlTxt to null
        commonJson.set("predParamType", null);
        commonJson.set("modCtrlTxt", null);
        return commonJson;
    };
    
    @Override
    public List<CommonJson> getRoleGroupPermission(Optional<String> optionalRoleGroupId, Optional<String> optionalActionType, boolean treeView) {
        // TODO Auto-generated method stub
        List<CommonJson> nodeCommonJsonList = new ArrayList<>();
        
        Consumer<CommonJson> addNodeCommonJsonList = commonJson -> {
            commonJson.getProps().remove("isRoot");
            commonJson.set("oldPermEffectiveDate", commonJson.get("permEffectiveDate"));
            commonJson.set("oldPermExpiryDate", commonJson.get("permExpiryDate"));
            nodeCommonJsonList.add(commonJson);
        };
        
        Function<EntityRelationshipDAO, CommonJson> mapToActionNode = entityRelationship -> {
            CommonJson commonJson = new CommonJson();
            String permStatusTypeDef = env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_DEFAULT);
            Optional<PermStatusDAO> optionalPermStatusDef = Optional.ofNullable(permStatusRepository.findOne(permStatusTypeDef));
            commonJson.set("nodeId", entityRelationship.getEntityRelationshipPK().getAction().getActionType());
            commonJson.set("nodeDesc", entityRelationship.getEntityRelationshipPK().getAction().getActionDesc());
            commonJson.set("nodeType", env.getRequiredProperty(AppConstants.RBAC_ACTION_NODE_TYPE));
            commonJson.set("parentNodeId", entityRelationship.getEntityRelationshipPK().getEntity().getEntityType());
            commonJson.set("permPredParam", null);
            commonJson.set("modCtrlTxt", null);
            optionalPermStatusDef.ifPresent(permStatusDef -> {
                commonJson.set("permStatusType", permStatusDef.getPermStatusType());
                commonJson.set("permStatusDesc", permStatusDef.getPermStatusDesc());
                commonJson.set("permEffectiveDate", null);
                commonJson.set("permExpiryDate", null);
            });
            entityRelationship.getPredicate().ifPresent(predicate -> {
                commonJson.set("permPredParam", mapToPermPredParamDef.apply(predicate.getPredParamDef()));
            });
            optionalPermStatusDef.orElseThrow(() -> new RuntimeException(String.format("Cannot retrieve default permission status type=%", permStatusTypeDef)));
            
            if(optionalRoleGroupId.isPresent()) {
                Optional<PermissionDAO> optionalPermission = permissionRepository.findByRoleGroupIdAndEntityTypeAndActionType(optionalRoleGroupId.get(), commonJson.get("parentNodeId"), commonJson.get("nodeId"));
                String roleGroupId = optionalRoleGroupId.get();
                String sysAdminRoleGroupId = env.getRequiredProperty(AppConstants.RBAC_ROLE_SYSADMIN);
                //String defaultRoleGroupId = env.getRequiredProperty(AppConstants.RBAC_ROLE_DEFAULT);
                if(!roleGroupId.contentEquals(sysAdminRoleGroupId) && optionalPermission.isPresent()) {
                    commonJson.set("permStatusType", optionalPermission.get().getPermStatus().getPermStatusType());
                    commonJson.set("permStatusDesc", optionalPermission.get().getPermStatus().getPermStatusDesc());
                    commonJson.set("permEffectiveDate", GeneralUtil.isBlankTimestamp(optionalPermission.get().getEffectiveDate())? null: GeneralUtil.getStringByDate(optionalPermission.get().getEffectiveDate()));
                    commonJson.set("permExpiryDate", GeneralUtil.isBlankTimestamp(optionalPermission.get().getExpiryDate())? null: GeneralUtil.getStringByDate(optionalPermission.get().getExpiryDate()));
                    optionalPermission.get().getPredParam().ifPresent(predParam -> {
                        commonJson.set("permPredParam", mapToPermPredParam.apply(predParam));
                    });
                    commonJson.set("modCtrlTxt", optionalPermission.get().getModCtrlTxt());
                } else if(roleGroupId.contentEquals(sysAdminRoleGroupId) /*|| roleGroupId.contentEquals(defaultRoleGroupId)*/) {
                    String permStatusTypeAllowed = env.getRequiredProperty(AppConstants.RBAC_PERM_STATUS_ALLOWED);
                    Optional<PermStatusDAO> optionalPermStatusAllowed = Optional.ofNullable(permStatusRepository.findOne(permStatusTypeAllowed));
                    optionalPermStatusAllowed.ifPresent(permStatusAllowed -> {
                        commonJson.set("permStatusType", permStatusAllowed.getPermStatusType());
                        commonJson.set("permStatusDesc", permStatusAllowed.getPermStatusDesc());
                    });
                    optionalPermStatusAllowed.orElseThrow(() -> new RuntimeException(String.format("Cannot retrieve allowed permission status type=%", permStatusTypeAllowed)));
                }
            }
            if(treeView) {
                commonJson.set("nodeChildren", Collections.emptyList());
            }
            log.debug(String.format("Create node=%s", commonJson.toString()));
            
            return commonJson;
        };
        
        List<EntityDAO> allEntityLCAList = new ArrayList<>();
        
        Optional.ofNullable(entityRepository.findRootEntity(sortEntityByEntityType)).ifPresent(rootEntityList -> {
            allEntityLCAList.addAll(rootEntityList);
        });
        
        Deque<CommonJson> entityNodeDeque = allEntityLCAList.stream()
                                                            .map(mapToEntityNode)
                                                            .distinct()
                                                            .collect(Collectors.toCollection(ArrayDeque::new));
        
        final List<EntityRelationshipDAO> entityRelationshipList = new ArrayList<>();
        List<CommonJson> nodeChildrenList;
        List<EntityDAO> childEntityList;
        
        //Tree breadth-first traversal
        while(!entityNodeDeque.isEmpty()) {
            //Pop entity node
            CommonJson entityNodeCommonJson = entityNodeDeque.removeFirst();
            entityRelationshipList.clear();;
            nodeChildrenList = Collections.emptyList();
            childEntityList = entityRepository.findByParentEntityType(entityNodeCommonJson.get("nodeId"), sortEntityByEntityType);
            
            if(!childEntityList.isEmpty()) {
                nodeChildrenList = childEntityList.stream()
                                                  .map(mapToEntityNode)
                                                  .collect(Collectors.toList());
                //reverse the node children as the children are processed as first in last out
                Collections.reverse(nodeChildrenList);
                //Push entity node children
                nodeChildrenList.stream().forEachOrdered(c -> entityNodeDeque.offerFirst(c));
                if(treeView) {
                    //Add entity node children (treeView)
                    entityNodeCommonJson.set("nodeChildren", nodeChildrenList);
                }
            }
            
            if(optionalActionType.isPresent()) {
                entityRelationshipRepository.findByEntityTypeAndActionType(entityNodeCommonJson.get("nodeId"), optionalActionType.get()).ifPresent(entityRelationship -> {
                    entityRelationshipList.add(entityRelationship);
                });
            } else {
                entityRelationshipList.addAll(entityRelationshipRepository.findByEntityType(entityNodeCommonJson.get("nodeId"), sortEntityRelationshipByActionType)); 
            }
            
            if(treeView) {
                //Add action node children (treeView)
                nodeChildrenList.addAll(entityRelationshipList.stream()
                                                        .map(mapToActionNode)
                                                        .distinct()
                                                        .collect(Collectors.toList()));
                entityNodeCommonJson.set("nodeChildren", nodeChildrenList);
            }
            
            if(treeView && entityNodeCommonJson.get("isRoot").contentEquals("true")) {
                //Add entity node (treeView)
                addNodeCommonJsonList.accept(entityNodeCommonJson);
            } else if(treeView && entityNodeCommonJson.get("isRoot").contentEquals("false")) {
                //Remove attribute "isRoot" for entity node children (treeView)
                entityNodeCommonJson.getProps().remove("isRoot");
            } else if(!treeView && !entityRelationshipList.isEmpty()) {
                //Add entity node once (non-treeView)
                addNodeCommonJsonList.accept(entityNodeCommonJson);
                //Add all related action node (non-treeView)
                entityRelationshipList.stream().forEachOrdered(entityRelationship -> {
                    addNodeCommonJsonList.accept(mapToActionNode.apply(entityRelationship));
                });
            } else if(!treeView && entityRelationshipList.isEmpty()) {
                //No users, add only entity node (non-treeView)
                addNodeCommonJsonList.accept(entityNodeCommonJson);
            }
        }
        
        return nodeCommonJsonList;
    }

    @Override
    public List<CommonJson> getPermissionPredParam(String username, String entityType, String actionType) {
        // TODO Auto-generated method stub
        List<String> roleGroupIdList = userRoleGroupRepository.findByUsername(username, null)
                                                                  .stream()
                                                                  .map(urg -> urg.getUserRoleGroupPK().getRoleGroup().getRoleGroupId())
                                                                  .collect(Collectors.toList());
        return permissionRepository.findByRoleGroupIdInAndEntityTypeAndActionType(roleGroupIdList, entityType, actionType)
                                   .stream().filter(p -> p.getPredParam().isPresent())
                                   .map(p -> p.getPredParam().get())
                                   .map(mapToPermPredParam).collect(Collectors.toList());
    }

}
