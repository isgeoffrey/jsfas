package jsfas.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jsfas.common.constants.ResponseCodeConstants;
import jsfas.common.constants.RestURIConstants;
import jsfas.common.json.CommonJson;
import jsfas.common.json.Response;
import jsfas.db.main.persistence.service.FileService;


@RestController
@ControllerAdvice
public class FileController extends CommonApiController {
	
	@Autowired
	FileService fileService;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	@RequestMapping(value = RestURIConstants.FILES_BY_ID, method = RequestMethod.GET)
	public Response getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable(name="id") String id) throws Exception {
		CommonJson outputFile = fileService.getAsset(id);
	    
	    if (outputFile.isEmpty()) {
	    	Response jsonResponse = new Response();
	    	jsonResponse.setMeta(ResponseCodeConstants.ERROR, "File not found!");
	    	return jsonResponse;
	    }
	    
	    String fileName = outputFile.get("file_name");
	    String mimeType =  outputFile.get("file_type");
		
	    log.debug("outputFile: {}", outputFile.props());
		
	    response.setContentType(mimeType + "; name=\"" + StringEscapeUtils.escapeJava(fileName) + "\"");
	    response.setHeader("Content-Disposition", "inline; filename=\"" + StringEscapeUtils.escapeJava(fileName) + "\"");
	    byte[] fileByte = outputFile.get("file_content", byte[].class);
	    
	    FileCopyUtils.copy(fileByte, response.getOutputStream());
	    
		return null;
	}
	
	@RequestMapping(value = RestURIConstants.FILES, method = RequestMethod.POST)
	public Response addAsset(HttpServletRequest request, MultipartFile file) throws Exception {
		Response response = new Response();
			
		CommonJson data = new CommonJson();
		data.set("asset", fileService.insertAsset(file, getOpPageName(request)));
		response.setData(data);
		
		return setSuccess(response);
	}
}
