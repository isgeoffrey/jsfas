package jsfas.common.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jsfas.db.main.persistence.domain.MessageTableDAO;
import jsfas.db.main.persistence.repository.MessageTableRepository;

/**
 * CtMessageUtil for JSP call
 * @author iswill
 * @since 09/01/2018
 */

public class Message {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String RESERVED = "[RESERVED]";
	private final Pattern pattern = Pattern.compile("\\$\\{.*?\\}");

	@Autowired
	private MessageTableRepository messageTableRepository;
	
	private String getMessage(String code, String... vars) {
		List<MessageTableDAO> messages = messageTableRepository.findByMessageCodeAllIgnoringCase(code);
		
		if (messages.isEmpty()) {
			return "";
		}
		
		String message = messages.get(0).getMessageText();
		
		//reserved message handling
		//this handling is for CT only
		//when the message begin with "[RESERVED]", the message with return empty string in result
		if (StringUtils.startsWith(message, RESERVED)) {
			return "";
		}
		
		for (int i = 0; i < vars.length; i++) {
			message = message.replace("&" + (i+1), vars[i]);
		}
		
		//multiple message vars handling
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) {
			String matchGroup = matcher.group();
			
			String includedCode = matchGroup.substring(2, matchGroup.length()-1);
			//log.info("includedCode: {}", includedCode);
			
			String[] includedCodeSplitArr = includedCode.split(",");
			
			String includeMessage = "";
					
			if (includedCodeSplitArr.length > 1) {
				String[] includeVars = new String[includedCodeSplitArr.length-1];
				
				for (int j=1; j<includedCodeSplitArr.length; j++) {
					includeVars[j-1] = StringUtils.trimToEmpty(includedCodeSplitArr[j]);
				}
				
				includeMessage = this.getMessage(StringUtils.trimToEmpty(includedCodeSplitArr[0]), includeVars);
			} else {
				includeMessage = this.getMessage(StringUtils.trimToEmpty(includedCodeSplitArr[0]));
			}
			
			message = message.replace(matchGroup, includeMessage);
		}
		
		return message;
	}
	
	public String get(String code, String... vars) {
		return getMessage(code, vars);
	}
	
	public String get(String code) {
		return getMessage(code);
	}
	
	public String getForHtml(String code, String... vars) {
		return getMessage(code, vars) + "<span class='pull-right'>(" + code + ")</span>";
	}
	
	public String getForHtml(String code) {
		return getMessage(code) + "<span class='pull-right'>(" + code + ")</span>";
	}
	
}
