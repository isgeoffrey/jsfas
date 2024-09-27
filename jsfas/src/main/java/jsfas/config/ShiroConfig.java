package jsfas.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.context.request.RequestContextListener;

import jsfas.common.condition.RBACCondition;
import jsfas.security.permission.RbacWildcardPermissionResolver;
import jsfas.security.realm.RbacRealm;

@Configuration
@Conditional(value = RBACCondition.class)
public class ShiroConfig {
    
	@Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    
    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        //Map<String, String> filters = new HashMap<>();
        //filters.put("/**", "perms");
        //shiroFilter.setFilterChainDefinitionMap(filters);
        //shiroFilter.setUnauthorizedUrl("/web/error");
        shiroFilter.setSecurityManager(securityManager());
        return shiroFilter;
    }
    
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        securityManager.setCacheManager(shiroCacheManager());
        return securityManager;
    }
    
    @Bean
    public CacheManager shiroCacheManager() {
        return new MemoryConstrainedCacheManager();
    }
    
    @Bean
    public Realm realm() {
        RbacRealm jpaRealm = new RbacRealm(shiroCacheManager());
        jpaRealm.setPermissionResolver(permissionResolver());
        return jpaRealm;
    }
    
    @Bean
    public PermissionResolver permissionResolver() {
        return new RbacWildcardPermissionResolver();
    }
    
    @Bean
    @DependsOn({ "lifecycleBeanPostProcessor" })
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
    
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());
        return advisor;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public OrderedRequestContextFilter requestContextFilter(){
        return new OrderedRequestContextFilter();
    } 
    
    @Bean 
    @ConditionalOnMissingBean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    } 
}
