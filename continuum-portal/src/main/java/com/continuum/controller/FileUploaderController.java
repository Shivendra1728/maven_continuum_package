package com.continuum.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.continuum.serviceImpl.FileUploadServiceImpl;

@RestController
public class FileUploaderController {

	@Autowired(required = true)
	private FileUploadServiceImpl fileuploadserviceimpl;

	@PostMapping("/upload-file")
	public String FileUploader(@RequestParam("data") MultipartFile data) throws IOException {
		System.out.println(data.getOriginalFilename());
		System.out.println(data.getName());
		System.out.println(data.getSize());
		System.out.println(data.getContentType());
		System.out.println(data.getBytes());
		return fileuploadserviceimpl.fileUploader(data);
	}
}
