package jsfas.db.main.persistence.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import jsfas.common.json.CommonJson;

public interface FileService {
	public CommonJson getAsset(String id);
	public CommonJson insertAsset(MultipartFile file, String opPageName) throws IOException;
}
