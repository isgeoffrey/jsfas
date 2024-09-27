package jsfas.db.main.persistence.service;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jsfas.common.json.CommonJson;
import jsfas.db.main.persistence.domain.ElUpldFileDAO;
import jsfas.db.main.persistence.repository.ElUpldFileRepository;
import jsfas.security.SecurityUtils;

public class FileEventHandler implements FileService {

	@Autowired
	ElUpldFileRepository elUpldFileRepository;
	
	@Override
	public CommonJson getAsset(String id) {
		CommonJson output = new CommonJson();
		ElUpldFileDAO fileDAO = elUpldFileRepository.findOne(id);
		
		if (fileDAO != null) {
			output
				.set("file_id", fileDAO.getId())
				.set("file_name", fileDAO.getFileName())
				.set("file_type", fileDAO.getFileType())
				.set("file_extension", fileDAO.getFileExtension())
				.set("file_content", fileDAO.getFileContent())
				.set("mod_ctrl_txt", fileDAO.getModCtrlTxt());
		}
		
		return output;
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public CommonJson insertAsset(MultipartFile file, String opPageName) throws IOException {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		ElUpldFileDAO fileDAO = new ElUpldFileDAO();
		
		fileDAO.setFileName(file.getOriginalFilename());
		fileDAO.setFileType(file.getContentType());
		fileDAO.setFileExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
		fileDAO.setFileContent(file.getBytes());
		
		fileDAO.setCreatUser(remoteUser);
		fileDAO.setChngUser(remoteUser);
		fileDAO.setOpPageNam(opPageName);
		
		elUpldFileRepository.save(fileDAO);
		
		return new CommonJson()
				.set("file_id", fileDAO.getId())
				.set("file_name", fileDAO.getFileName())
				.set("file_type", fileDAO.getFileType())
				.set("file_extension", fileDAO.getFileExtension())
				.set("mod_ctrl_txt", fileDAO.getModCtrlTxt());
	}

}
