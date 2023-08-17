package com.continuum.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.continuum.service.AzureBlobService;
import com.continuum.tenant.repos.entity.ReturnOrderItem;

@RestController
public class FileUploadController {

	@Autowired
	AzureBlobService azureBlobService;

	@PostMapping("/upload-file")
	public String fileUploaders(MultipartFile[] data, @RequestParam("rmaNo") String rmaNo, ReturnOrderItem returnOrderItemId) throws IOException {

		azureBlobService.uploadFiles(data, rmaNo, returnOrderItemId);

		return "File Upload Sucessfully";
	}

}
