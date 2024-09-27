package jsfas;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import jsfas.JsfasApplication;
import jsfas.common.constants.AppConstants;
import jsfas.db.main.persistence.service.CommonRoutineEventHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JsfasApplication.class)
@WebAppConfiguration
public class JsfasTestSendEmail {

	
    @Inject
    private Environment env;
    
    @Autowired
    CommonRoutineEventHandler commonRoutineEventHandler;
    
	@Test
	public void contextLoads() {
		System.out.println("Test start");
		
		commonRoutineEventHandler.sendEmail("inform@apjcorp.com", "Testing", "isod02@ust.hk", "","jsfas Testing send email", "testing Email", true);
		System.out.println("Test end");
	}

}
