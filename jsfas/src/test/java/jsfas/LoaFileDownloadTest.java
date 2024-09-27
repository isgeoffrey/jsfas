package jsfas;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
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
import jsfas.db.main.persistence.domain.ElUpldFileDAO;
import jsfas.db.main.persistence.service.CommonRoutineEventHandler;
import jsfas.db.main.persistence.service.LoaEventHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JsfasApplication.class)
@WebAppConfiguration
public class LoaFileDownloadTest {

	
    @Inject
    private Environment env;
    
    @Autowired
    LoaEventHandler loaEventHandler;
    
	@Test
	public void contextLoads() {
		System.out.println("Test start");

//		Map<String,Object> param = loaEventHandler.getFileDetailsById("6251bf12-f6d1-4fcc-b8ed-0ef76703d2cc");
		
//		System.out.println(param.toString());
		
//		File downloadFile = new File();
		try {
//			ElUpldFileDAO dao = loaEventHandler.generateLoA(param, "", "isod01");
			
//			FileUtils.writeByteArrayToFile(new File("C:/Users/isgeoffrey/Downloads/test_download.pdf"), dao.getFileContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Test end");
	}

}
