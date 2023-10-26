package com.continuum.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.continuum.service.AzureBlobService;

@RestController
@RequestMapping("/api")
public class CsvFileUploadController {

	@Autowired
	AzureBlobService azureBlobService;

	@PostMapping("/upload-csv")
	public List<Map<String, String>> uploadCSVToBlob(List<MultipartFile> files) throws Exception {
		List<Map<String, String>> urls = azureBlobService.uploadCSV(files);

		return urls;
	}

}
