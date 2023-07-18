package com.continuum.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.continuum.repos.entity.ReturnOrderItem;
import com.continuum.service.FileUploadService;
import com.continuum.serviceImpl.AzureBlobStorageService;

@RestController
public class FileUploadController {

	@Autowired
	FileUploadService fileuploadservice;
	@Autowired
	AzureBlobStorageService azureBlobStorageService;

	@PostMapping("/upload-file")
	public String fileUploaders(MultipartFile[] data, @RequestParam("rmaNo") String rmaNo) throws IOException {

		azureBlobStorageService.uploadFiles(data, rmaNo);

		return "File Upload Sucessfully";
	}

}
