package com.continuum.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.continuum.service.AzureBlobService;
import com.continuum.service.FileUploadService;

@RestController
public class FileUploadController {

	@Autowired
	AzureBlobService azureBlobService;
  
	@Autowired
	FileUploadService fileUploadService;

	@PostMapping("/upload-file")
  public List<Map<String,String>> fileUploaders(MultipartFile[] data, @RequestParam("customerId") String customerId
	) throws Exception {

		List<Map<String, String>> url = azureBlobService.uploadFiles(data, customerId);
		
    return url;

	}

	@PostMapping("/user-img")
	public String uploadImg(MultipartFile data, @RequestParam("id") Long userId) throws IOException {
		return fileUploadService.fileUploader(data, userId);
	}
}
