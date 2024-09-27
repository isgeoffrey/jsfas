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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import jsfas.common.constants.AppConstants;
import jsfas.common.json.CommonJson;
import jsfas.db.rbac.persistence.domain.RoleGroupDAO;
import jsfas.db.rbac.persistence.domain.UserRoleGroupDAO;
import jsfas.db.rbac.persistence.repository.RoleGroupRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;

public class UserRoleGroupEventHandler implements UserRoleGroupService {

    @Autowired
    private RoleGroupRepository roleGroupRepository;
    
    @Autowired
    private UserRoleGroupRepository userRoleGroupRepository;
    
    @Autowired
    private Environment env;
    
    private Sort sortRoleGroupByRoleGroupDesc = new Sort("roleGroupDesc");
    
    private Sort sortUserRoleGroupByUserName = new Sort("userRoleGroupPK.userProfileHeader.userName");

    private Function<RoleGroupDAO, CommonJson> mapToRoleNode = roleGroup -> {
        CommonJson commonJson = new CommonJson();
        Optional<RoleGroupDAO> optionalParentRoleGroup = roleGroup.getParentRoleGroup();
        commonJson.set("nodeId", roleGroup.getRoleGroupId());
        commonJson.set("nodeDesc", roleGroup.getRoleGroupDesc());
        commonJson.set("nodeDescOld", roleGroup.getRoleGroupDesc());
        commonJson.set("nodeType", env.getRequiredProperty(AppConstants.RBAC_ROLE_NODE_TYPE));
        commonJson.set("modCtrlTxt", roleGroup.getModCtrlTxt());
        if(optionalParentRoleGroup.isPresent()) {
            commonJson.set("parentNodeId", optionalParentRoleGroup.get().getRoleGroupId());
            commonJson.set("parentNodeIdOld", optionalParentRoleGroup.get().getRoleGroupId());
            commonJson.set("isRoot", "false");
        } else {
            commonJson.set("parentNodeId", null);
            commonJson.set("parentNodeIdOld", null);
            commonJson.set("isRoot", "true");
        }
        return commonJson;
    };
    
    private Function<RoleGroupDAO, CommonJson> mapToRootRoleNode = roleGroup -> {
        CommonJson commonJson = mapToRoleNode.apply(roleGroup);
        //Override as root
        commonJson.set("isRoot", "true");
        commonJson.set("parentNodeId", null);
        commonJson.set("parentNodeIdOld", null);
        return commonJson;
    };
    
    @Override
    @Transactional(value = "transactionManagerJsfasMain", readOnly = true)
    public List<CommonJson> getUserRoleGroup(Optional<String> optionalUserName, boolean treeView) {
        // TODO Auto-generated method stub
        List<CommonJson> nodeCommonJsonList = new ArrayList<>();
        
        Consumer<CommonJson> addNodeCommonJsonList = commonJson -> {
            commonJson.set("isNew", false);
            commonJson.getProps().remove("isRoot");
            nodeCommonJsonList.add(commonJson);
        };
        
        Function<UserRoleGroupDAO, CommonJson> mapToUserNode = userRoleGroup -> {
            CommonJson commonJson = new CommonJson();
            commonJson.set("nodeId", userRoleGroup.getUserRoleGroupPK().getUserProfileHeader().getUserName());
            commonJson.set("nodeDesc", userRoleGroup.getUserRoleGroupPK().getUserProfileHeader().getUserName());
            commonJson.set("nodeType", env.getRequiredProperty(AppConstants.RBAC_USER_NODE_TYPE));
            commonJson.set("modCtrlTxt", userRoleGroup.getModCtrlTxt());
            commonJson.set("parentNodeId", userRoleGroup.getUserRoleGroupPK().getRoleGroup().getRoleGroupId());
            commonJson.set("parentNodeIdOld", userRoleGroup.getUserRoleGroupPK().getRoleGroup().getRoleGroupId());
            if(treeView) {
                commonJson.set("nodeChildren", Collections.emptyList());
            }
            
            return commonJson;
        };
        
        List<RoleGroupDAO> allRoleGroupLCAList = new ArrayList<>();
        List<RoleGroupDAO> rootRoleGroupList = roleGroupRepository.findRootRoleGroup(sortRoleGroupByRoleGroupDesc);
        if(optionalUserName.isPresent()) {
            rootRoleGroupList.stream().forEach(roleGroup -> {
                //LCA node for specific user
                allRoleGroupLCAList.addAll(findLCARoleGroupByUsername(optionalUserName.get(), roleGroup));
            });
        } else {
            //all root node for all users
            allRoleGroupLCAList.addAll(rootRoleGroupList);
        }
        
        Deque<CommonJson> roleNodeDeque = allRoleGroupLCAList.stream()
                                                             .map(mapToRootRoleNode)
                                                             .distinct()
                                                             .collect(Collectors.toCollection(ArrayDeque::new));

        String roleGroupIdDef = env.getRequiredProperty(AppConstants.RBAC_ROLE_DEFAULT);
        
        List<UserRoleGroupDAO> userRoleGroupList;
        List<CommonJson> nodeChildrenList;
        List<RoleGroupDAO> childRoleGroupList;
        
        //Tree breadth-first traversal
        while(!roleNodeDeque.isEmpty()) {
            //Pop role node
            CommonJson roleNodeCommonJson = roleNodeDeque.removeFirst();
            userRoleGroupList = Collections.emptyList();
            nodeChildrenList = Collections.emptyList();
            childRoleGroupList = roleGroupRepository.findByParentRoleGroupId(roleNodeCommonJson.get("nodeId"), sortRoleGroupByRoleGroupDesc);
            
            if(roleNodeCommonJson.get("nodeId").equals(roleGroupIdDef))
            	continue;
            
            if(!childRoleGroupList.isEmpty()) {
                nodeChildrenList = childRoleGroupList.stream()
                                                     .map(mapToRoleNode)
                                                     .collect(Collectors.toList());
                //reverse the node children as the children are processed as first in last out
                Collections.reverse(nodeChildrenList);
                //Push role node children
                nodeChildrenList.stream().forEachOrdered(c -> roleNodeDeque.offerFirst(c));
                if(treeView) {
                    //Add role node children (treeView)
                    roleNodeCommonJson.set("nodeChildren", nodeChildrenList);
                }
            }
            
            userRoleGroupList = userRoleGroupRepository.findByRoleGroupId(roleNodeCommonJson.get("nodeId"), sortUserRoleGroupByUserName);
            
                
            /* Uncomment to exclude other user in root node
            //If exist specific user, then include only the specific user for root node
            if(optionalUserName.isPresent() 
                    && userRoleGroupRepository.countByUsernameAndRoleGroupId(optionalUserName.get(), roleNodeCommonJson.get("nodeId")) != 0
                    && roleNodeCommonJson.get("isRoot").contentEquals("true")) {
                userRoleGroupList = userRoleGroupList.stream()
                                                     .filter(urg -> urg.getUserRoleGroupPK().getUserProfileHeader().getUserNam().contentEquals(optionalUserName.get()))
                                                     .collect(Collectors.toList());
            
            }
            */
            
            
            if(treeView) {
                //Add user node children (treeView)
                nodeChildrenList.addAll(userRoleGroupList.stream()
                                                         .map(mapToUserNode)
                                                         .distinct()
                                                         .collect(Collectors.toList()));
                roleNodeCommonJson.set("nodeChildren", nodeChildrenList);
            }
            
            if(treeView && roleNodeCommonJson.get("isRoot").contentEquals("true")) {
                //Add role node (treeView)
                addNodeCommonJsonList.accept(roleNodeCommonJson);
            } else if(treeView && roleNodeCommonJson.get("isRoot").contentEquals("false")) {
                //Remove attribute "isRoot" for role node children (treeView)
                roleNodeCommonJson.getProps().remove("isRoot");
            } else if(!treeView && !userRoleGroupList.isEmpty()) {
                //Add role node once (non-treeView)
                addNodeCommonJsonList.accept(roleNodeCommonJson);
                //Add all related user node (non-treeView)
                userRoleGroupList.stream().forEachOrdered(userRoleGroup -> {
                    addNodeCommonJsonList.accept(mapToUserNode.apply(userRoleGroup));
                });
            } else if(!treeView && userRoleGroupList.isEmpty()) {
                //No users, add only role node (non-treeView)
                addNodeCommonJsonList.accept(roleNodeCommonJson);
            }
        }
        
        return nodeCommonJsonList;
    }
    
    /*
    private Optional<RoleGroupDAO> findUserRootRoleGroup(UserRoleGroupDAO userRoleGroup) {
        Optional<RoleGroupDAO> optionalParentRoleGroup = roleGroupRepository.findParentRoleGroupByRoleGroupId(userRoleGroup.getUserRoleGroupPK().getRoleGroup().getRoleGroupId(), null);
        Optional<RoleGroupDAO> optionalRoleGroup = Optional.empty();
        
        while (optionalParentRoleGroup.isPresent()) {
            optionalRoleGroup = optionalParentRoleGroup;
            optionalParentRoleGroup = roleGroupRepository.findParentRoleGroupByRoleGroupId(optionalParentRoleGroup.get().getRoleGroupId(), null);
        }
        
        return optionalRoleGroup;
    }
    */
    
    private List<RoleGroupDAO> findLCARoleGroupByUsername(String username, RoleGroupDAO roleGroup) {
        List<RoleGroupDAO> roleGroupList = new ArrayList<>();
        
        if(userRoleGroupRepository.countByUsername(username) != 0) {
            Deque<RoleGroupDAO> roleGroupDeque = new ArrayDeque<>();
            
            roleGroupDeque.offerFirst(roleGroup);
            
            //Tree breadth-first traversal
            while(!roleGroupDeque.isEmpty()) {
                roleGroup = roleGroupDeque.removeFirst();
                
                if(userRoleGroupRepository.countByUsernameAndRoleGroupId(username, roleGroup.getRoleGroupId()) != 0) {
                    roleGroupList.add(roleGroup);
                    continue;
                }
                
                roleGroupRepository.findByParentRoleGroupId(roleGroup.getRoleGroupId(), sortRoleGroupByRoleGroupDesc)
                                   .stream().forEachOrdered(c -> roleGroupDeque.offerFirst(c));
            }
        }
        
        return roleGroupList;
    }

}
