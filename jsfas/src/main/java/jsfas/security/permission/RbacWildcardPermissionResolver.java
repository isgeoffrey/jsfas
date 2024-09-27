package jsfas.security.permission;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

public class RbacWildcardPermissionResolver implements PermissionResolver {
    
    @Override
    public Permission resolvePermission(String permissionString) {
        return new RbacWildcardPermission(permissionString);
    }
}
