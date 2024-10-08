package jsfas.db.main.persistence.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;

import jsfas.common.excel.XCol;
import jsfas.common.json.CommonJson;

public interface FileService {
	public ArrayList<HashMap<String, Object>> getDataFromUploadFile(MultipartFile uploadFile,
			ArrayList<XCol> cols, int dataStartRow) throws Exception;
}
