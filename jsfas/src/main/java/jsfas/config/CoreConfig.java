package jsfas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jsfas.db.jssv.persistence.service.OAuth2RestEventHandler;
import jsfas.db.jssv.persistence.service.OAuth2RestService;
import jsfas.db.main.persistence.service.AuthEventHandler;
import jsfas.db.main.persistence.service.AuthService;
import jsfas.db.main.persistence.service.CommonRoutineEventHandler;
import jsfas.db.main.persistence.service.CommonRoutineService;


import jsfas.db.main.persistence.service.FileEventHandler;
import jsfas.db.main.persistence.service.FileService;
import jsfas.db.main.persistence.service.GeneralApiEventHandler;
import jsfas.db.main.persistence.service.GeneralApiService;

import jsfas.db.main.persistence.service.RedisEventHandler;
import jsfas.db.main.persistence.service.RedisService;

import jsfas.db.main.persistence.service.StocktakeEventHandler;
import jsfas.db.main.persistence.service.StocktakeService;
import jsfas.db.main.persistence.service.StocktakeStagingService;
import jsfas.db.main.persistence.service.StocktakeStagingEventHandler;
// import jsfas.db.main.persistence.service.SchedulerEventHandler;
// import jsfas.db.main.persistence.service.SchedulerService;
//import jsfas.db.main.persistence.service.UserProfileEventHandler;
//import jsfas.db.main.persistence.service.UserProfileService;
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

//	@Bean
//	public SchedulerService schedulerService() {
//       return new SchedulerEventHandler();
//	}
	
	/********** examples (remove for new project) **********/
	
//	@Bean
//	public UserProfileService userProfileService() {
//       return new UserProfileEventHandler();
//	}
//	
//	@Bean
//    public UserRoleGroupService userRoleGroupService() {
//        return new UserRoleGroupEventHandler();
//    }
//    
//    @Bean
//    public EntityRelationshipService entityRelationshipService() {
//        return new EntityRelationshipEventHandler();
//    }
//    
//    @Bean
//    public PermissionService permissionService() {
//        return new PermissionEventHandler();
//    }
//    
//    @Bean
//    public CustomPermissionService customPermissionService() {
//        return new CustomPermissionEventHandler();
//    }
//    
//    @Bean
//    public PredicateService predicateService() {
//        return new PredicateEventHandler();
//    }
//    
    
     @Bean
     public FileService fileService() {
     	return new FileEventHandler();
     }
    
    
    @Bean
    public GeneralApiService gGeneralApiService() {
    	return new GeneralApiEventHandler();
    }
    
	@Bean
	public StocktakeService stocktakeService(){
		return new StocktakeEventHandler();
	}

	@Bean
	public StocktakeStagingService stocktakeStagingService(){
		return new StocktakeStagingEventHandler();
	}
    
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
