package com.continuum.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AzureBlobService {

	List<String> uploadFiles(MultipartFile[] data, String customerId) throws IOException, Exception;

}