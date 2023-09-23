package com.continuum.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface AzureBlobService {

	List<Map<String, String>> uploadFiles(MultipartFile[] data, String customerId) throws IOException, Exception;

}