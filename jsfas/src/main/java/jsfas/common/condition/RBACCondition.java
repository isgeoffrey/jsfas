package jsfas.common.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import jsfas.common.constants.AppConstants;

public class RBACCondition implements Condition {
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // TODO Auto-generated method stub
        return context.getEnvironment().getProperty(AppConstants.RBAC_PERM_ENABLED, Boolean.class, false);
    }

}
