package jsfas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jsfas.db.jssv.persistence.service.OAuth2RestEventHandler;
import jsfas.db.jssv.persistence.service.OAuth2RestService;
import jsfas.db.main.persistence.service.AuthEventHandler;
import jsfas.db.main.persistence.service.AuthService;
import jsfas.db.main.persistence.service.CommonRoutineEventHandler;
import jsfas.db.main.persistence.service.CommonRoutineService;
import jsfas.db.main.persistence.service.EmailNotificationEventHandler;
import jsfas.db.main.persistence.service.EmailNotificationService;
// import jsfas.db.main.persistence.service.ExtraLoadApplicationEventHandler;
// import jsfas.db.main.persistence.service.ExtraLoadApplicationService;
// import jsfas.db.main.persistence.service.ExtraLoadPaymentEventHandler;
// import jsfas.db.main.persistence.service.ExtraLoadPaymentService;
// import jsfas.db.main.persistence.service.ExtraLoadTypeEventHandler;
// import jsfas.db.main.persistence.service.ExtraLoadTypeService;
import jsfas.db.main.persistence.service.FileEventHandler;
import jsfas.db.main.persistence.service.FileService;
import jsfas.db.main.persistence.service.GeneralApiEventHandler;
import jsfas.db.main.persistence.service.GeneralApiService;
// import jsfas.db.main.persistence.service.LoaEventHandler;
// import jsfas.db.main.persistence.service.LoaService;
import jsfas.db.main.persistence.service.RedisEventHandler;
import jsfas.db.main.persistence.service.RedisService;
// import jsfas.db.main.persistence.service.ReportEventHandler;
// import jsfas.db.main.persistence.service.ReportService;
import jsfas.db.main.persistence.service.SchedulerEventHandler;
import jsfas.db.main.persistence.service.SchedulerService;
import jsfas.db.main.persistence.service.UserProfileEventHandler;
import jsfas.db.main.persistence.service.UserProfileService;
import jsfas.db.rbac.persistence.service.CustomPermissionEventHandler;
import jsfas.db.rbac.persistence.service.CustomPermissionService;
import jsfas.db.rbac.persistence.service.EntityRelationshipEventHandler;
import jsfas.db.rbac.persistence.service.EntityRelationshipService;
import jsfas.db.rbac.persistence.service.PermissionEventHandler;
import jsfas.db.rbac.persistence.service.PermissionService;
import jsfas.db.rbac.persistence.service.PredicateEventHandler;
import jsfas.db.rbac.persistence.service.PredicateService;
import jsfas.db.rbac.persistence.service.RbacCommandEventHandler;
import jsfas.db.rbac.persistence.service.RbacCommandService;
import jsfas.db.rbac.persistence.service.UserRoleGroupEventHandler;
import jsfas.db.rbac.persistence.service.UserRoleGroupService;
import net.bytebuddy.asm.Advice.This;

/**
 * @author iseric
 * @since 12/5/2016
 * @see This is a Class for Dependency Injection Bean setting
 */
@Configuration
public class CoreConfig {

	@Bean
	public CommonRoutineService commonRoutineService() {
       return new CommonRoutineEventHandler();
	}
	

    @Bean
    public AuthService authService() {
        return new AuthEventHandler();
    }

    @Bean
    public RedisService redisService() {
        return new RedisEventHandler();
    }

	@Bean
	public SchedulerService schedulerService() {
       return new SchedulerEventHandler();
	}
	
	/********** examples (remove for new project) **********/
	
	@Bean
	public UserProfileService userProfileService() {
       return new UserProfileEventHandler();
	}
	
	@Bean
    public UserRoleGroupService userRoleGroupService() {
        return new UserRoleGroupEventHandler();
    }
    
    @Bean
    public EntityRelationshipService entityRelationshipService() {
        return new EntityRelationshipEventHandler();
    }
    
    @Bean
    public PermissionService permissionService() {
        return new PermissionEventHandler();
    }
    
    @Bean
    public CustomPermissionService customPermissionService() {
        return new CustomPermissionEventHandler();
    }
    
    @Bean
    public PredicateService predicateService() {
        return new PredicateEventHandler();
    }
    
    @Bean 
    public RbacCommandService rbacCommandService() {
        return new RbacCommandEventHandler();
    }
    
    // @Bean
    // public FileService fileService() {
    // 	return new FileEventHandler();
    // }
    
    // @Bean
    // public ExtraLoadTypeService extraLoadTypeService() {
    // 	return new ExtraLoadTypeEventHandler();
    // }
    
    // @Bean
    // public GeneralApiService gGeneralApiService() {
    // 	return new GeneralApiEventHandler();
    // }
    
    // @Bean
    // public ExtraLoadApplicationService extraLoadApplicationService() {
    // 	return new ExtraLoadApplicationEventHandler();
    // }
    
    // @Bean
    // public LoaService loaService() {
    // 	return new LoaEventHandler();
    // }
    
    // @Bean
    // public ExtraLoadPaymentService extraLoadPaymentService() {
    // 	return new ExtraLoadPaymentEventHandler();
    // }
    
    // @Bean
    // public EmailNotificationService emailNotificationService() {
    // 	return new EmailNotificationEventHandler();
    // }
    
    // @Bean
    // public ReportService reportService() {
    // 	return new ReportEventHandler();
    // }
    
    @Bean
    public OAuth2RestService oAuth2RestService() {
    	return new OAuth2RestEventHandler();
    }
}
