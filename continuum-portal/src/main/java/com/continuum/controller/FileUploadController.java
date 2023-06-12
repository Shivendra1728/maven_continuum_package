package com.continuum.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.continuum.repos.entity.ReturnOrderItem;
import com.continuum.service.FileUploadService;

@RestController
public class FileUploadController {

	@Autowired
	FileUploadService fileuploadservice;

	@PostMapping("/upload-file")
	public String fileUploader(@RequestParam("data") MultipartFile data, ReturnOrderItem returnOrderItemId)
			throws IOException {
		return fileuploadservice.fileUploader(data, returnOrderItemId);
	}

}
