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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JsfasApplication.class)
@WebAppConfiguration
public class JsfasApplicationTests {

	
    @Inject
    private Environment env;
    
	@Test
	public void contextLoads() {
		System.out.println("Test start");
		
		System.out.println("Test end");
	}

}
