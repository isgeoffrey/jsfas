package jsfas.security.permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

public class RbacWildcardPermission extends WildcardPermission {

    /**
     * 
     */
    private static final long serialVersionUID = -5412630582491850566L;
    
    private Optional<BooleanSupplier> optionalPredicate;
    
    public RbacWildcardPermission() {
        super();
        optionalPredicate = Optional.empty();
    }
    
    public RbacWildcardPermission(String wildcardString) {
        this(wildcardString, WildcardPermission.DEFAULT_CASE_SENSITIVE);
    }
    
    public RbacWildcardPermission(String wildcardString, BooleanSupplier predicate) {
        this(wildcardString, predicate, WildcardPermission.DEFAULT_CASE_SENSITIVE);
    }
    
    public RbacWildcardPermission(String wildcardString, boolean caseSensitive) {
        this(wildcardString, null, caseSensitive);
    }
    
    public RbacWildcardPermission(String wildcardString, BooleanSupplier predicate, boolean caseSensitive) {
        super(wildcardString, caseSensitive);
        optionalPredicate = Optional.ofNullable(predicate);
    }
    
    @Override
    public boolean implies(Permission p) {
        // By default only supports comparisons with other CustomWildcardPermissions
        if (!(p instanceof RbacWildcardPermission)) {
            return false;
        }

        //If exist predicate, evaluate the predicate first (short-circuit evaluation)
        if(optionalPredicate.isPresent() && !optionalPredicate.get().getAsBoolean()) {
            return false;
        }
        
        RbacWildcardPermission wp = (RbacWildcardPermission) p;

        List<Set<String>> otherParts = wp.getParts();
        List<Set<String>> thisParts = getParts();

        int i = 0;
        for (Set<String> thisPart : thisParts) {
            // If other permission has less parts than this permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if (otherParts.size() - 1 < i) {
                return true;
            } else {
                Set<String> otherPart = otherParts.get(i);
                if (!thisPart.contains(WILDCARD_TOKEN) && !otherPart.contains(WILDCARD_TOKEN) && !otherPart.containsAll(thisPart)) {
                    return false;
                }
                i++;
            }
        }
        
        // If other permission has more parts than this permission parts, implies as false
        if(!thisParts.get(i - 1).contains(WILDCARD_TOKEN) && i < otherParts.size()) {
            return false;
        }

        return true;
    }

}
