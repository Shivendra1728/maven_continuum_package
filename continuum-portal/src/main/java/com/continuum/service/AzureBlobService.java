package com.continuum.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface AzureBlobService {

	List<Map<String, String>> uploadFiles(List<MultipartFile> data, String customerId) throws IOException, Exception;

	List<Map<String, String>> uploadCSV(List<MultipartFile> files) throws Exception;

}