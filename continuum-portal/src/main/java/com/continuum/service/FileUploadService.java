package com.continuum.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

	public String fileUploader(MultipartFile data, Long userId);
}
