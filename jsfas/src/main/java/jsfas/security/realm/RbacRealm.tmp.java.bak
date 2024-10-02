package jsfas.security.realm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import jsfas.common.constants.AppConstants;
import jsfas.common.json.CommonJson;
import jsfas.db.rbac.persistence.domain.EntityDAO;
import jsfas.db.rbac.persistence.domain.PermissionDAO;
import jsfas.db.rbac.persistence.domain.UserRoleGroupDAO;
import jsfas.db.rbac.persistence.repository.EntityRepository;
import jsfas.db.rbac.persistence.repository.PermissionRepository;
import jsfas.db.rbac.persistence.repository.RoleGroupRepository;
import jsfas.db.rbac.persistence.repository.UserRoleGroupRepository;
import jsfas.db.rbac.persistence.service.PredicateService;
import jsfas.db.rbac.persistence.service.RbacCommandService;
import jsfas.security.RbacCmdType;
import jsfas.security.permission.RbacWildcardPermission;

public class RbacRealm extends AuthorizingRealm {

    @Autowired
    private ObjectFactory<UserRoleGroupRepository> userRoleGroupRepositoryObjectFactory;
    
    @Autowired
    private ObjectFactory<RoleGroupRepository> roleGroupRepositoryObjectFactory;
    
    @Autowired
    private ObjectFactory<PermissionRepository> permissionRepositoryObjectFactory;
    
    @Autowired
    private ObjectFactory<EntityRepository> entityRepositoryObjectFactory;
    
    @Autowired
    private ObjectFactory<PredicateService> predicateServiceObjectFactory;
    
    @Autowired
    private ObjectFactory<RbacCommandService> rbacCommandServiceObjectFactory;
    
    @Autowired
    private Environment env;
    
    private List<String> rolePermStatusExcludeList;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private Deque<EntityDAO> findEntityFullPath(EntityDAO entity) {
        Optional<EntityDAO> optionalParentEntity = Optional.ofNullable(entity);
        Deque<EntityDAO> deque = new ArrayDeque<>();
        
        while(optionalParentEntity.isPresent()) {
            deque.offerFirst(optionalParentEntity.get());
            optionalParentEntity = entityRepositoryObjectFactory.getObject().findParentEntityByEntityType(optionalParentEntity.get().getEntityType());
        }
        
        return deque;
    }
    
    public RbacRealm() {
        super();
    }
    
    public RbacRealm(CacheManager cacheManager) {
        super(cacheManager);
    }
    
    @Override
    protected void onInit() {
        rolePermStatusExcludeList = Arrays.asList(env.getProperty(AppConstants.RBAC_PERM_STATUS_REJECTED, "").split("\\|"));
//        log.info("Exclude perm status {}", rolePermStatusExcludeList.toString());
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
//        log.info(token.toString());
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        return new SimpleAuthenticationInfo(username, token.getCredentials(), getName());
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String roleGroupIdDef = env.getRequiredProperty(AppConstants.RBAC_ROLE_DEFAULT);
        String username = (String) getAvailablePrincipal(principals);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        
        List<UserRoleGroupDAO> userRoleGroupList = userRoleGroupRepositoryObjectFactory.getObject().findByUsername(username, null);
        if(userRoleGroupList.isEmpty() 
                || userRoleGroupList.stream().noneMatch(urg -> urg.getUserRoleGroupPK().getRoleGroup().getRoleGroupId().contentEquals(roleGroupIdDef))) {
            CommonJson cmdJson = new CommonJson();
            CommonJson cmdParams = new CommonJson();
            cmdJson.set("cmdType", RbacCmdType.ADD_USER.name());
            cmdParams.set("parentNodeId", roleGroupIdDef);
            cmdParams.set("parentNodeIdOld", cmdParams.get("parentNodeId"));
            cmdParams.set("nodeId", "N00001");
            cmdParams.set("nodeDesc", username);
            cmdParams.set("isNew", true);
            cmdParams.set("nodeType", env.getRequiredProperty(AppConstants.RBAC_USER_NODE_TYPE));
            cmdParams.set("modCtrlTxt", null);
            cmdJson.set("cmdParams", cmdParams);
            try {
                log.debug(String.format("Add guest role for user=%s", username));
                //do not check permission, as it will cause chicken-egg problem which lead to stack overflow error
                rbacCommandServiceObjectFactory.getObject().processCommandList(Arrays.asList(cmdJson), "SYSTEM", false);
                userRoleGroupList = userRoleGroupRepositoryObjectFactory.getObject().findByUsername(username, null);
                if(userRoleGroupList.isEmpty()) {
                    throw new AuthorizationException(String.format("Cannot find roles for user=%s", username));
                }
            } catch (Exception ex) {
                String errorMsg = String.format("Cannot add guest user cmd=%s", cmdJson);
                log.error(errorMsg, ex);
                throw new AuthorizationException(errorMsg);
            }
        }
        Set<String> roleGroupIdSet = userRoleGroupList.stream()
                                                      .map(urg -> urg.getUserRoleGroupPK().getRoleGroup().getRoleGroupId())
                                                      .collect(Collectors.toSet());
        boolean isSysAdmin = roleGroupIdSet.stream().anyMatch(roleGroupId -> roleGroupRepositoryObjectFactory.getObject().findRootRoleGroupByRoleGroupId(roleGroupId).isPresent());
        List<PermissionDAO> permissionList = permissionRepositoryObjectFactory.getObject().findByRoleGroupIdIn(roleGroupIdSet, null);
        
        log.info("Add user {} role {}", username, roleGroupIdSet.toString());
        info.addRoles(roleGroupIdSet);
        if(!isSysAdmin && !permissionList.isEmpty()) {
            List<PermissionDAO> permissionFilterList = permissionList.stream()
                                                                     .filter(p -> !rolePermStatusExcludeList.contains(p.getPermStatus().getPermStatusType()))
                                                                     .collect(Collectors.toList());
            for(PermissionDAO permission: permissionFilterList) {
                List<String> permStringList = new ArrayList<>();
                Deque<EntityDAO> deque = findEntityFullPath(permission.getPermissionPK().getEntity());
                /* 
                 * <Entity Lv1>[:<Action 1>,<Action 2>,...,<Action N>:<Entity Lv2>:<Entity Lv3>:...<Entity LvN>] 
                 */
                if(!deque.isEmpty()) {
                    permStringList.add(deque.removeFirst().getEntityType());
                    permStringList.add(permission.getPermissionPK().getAction().getActionType());
                    permStringList.addAll(deque.stream().map(EntityDAO::getEntityType).collect(Collectors.toList()));
                    //Create permission with static permission and runtime predicate
                    BooleanSupplier bs = () -> predicateServiceObjectFactory.getObject().evalPredicate(permission.getPredParam());
                    WildcardPermission perm = new RbacWildcardPermission(String.join(":", permStringList), bs);
                    info.addObjectPermission(perm);
                    log.debug("Add user {} permission {}", username, perm.toString());
                }
            }
        } else if(isSysAdmin) {
            entityRepositoryObjectFactory.getObject().findRootEntity(null).stream().forEach(e -> {
                WildcardPermission perm = new RbacWildcardPermission(String.format("%s:*", e.getEntityType()));
                info.addObjectPermission(perm);
                log.debug("Add user {} permission {}", username, perm.toString());
            });
        }
        
        return info;
    }
    
    public void clearAllCachedAuthorizationInfo() {
        Optional.ofNullable(this.getAuthorizationCache()).ifPresent(cache -> {
            cache.clear();
        });
    }
    
    public void clearAllCachedAuthenticationInfo() {
        Optional.ofNullable(this.getAuthenticationCache()).ifPresent(cache -> {
            cache.clear();
        });
    }
    
    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
  
}
