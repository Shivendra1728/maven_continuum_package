package com.continuum.service;

import org.springframework.web.multipart.MultipartFile;

import com.continuum.repos.entity.ReturnOrderItem;

public interface FileUploadService {

	public String fileUploader(MultipartFile data, ReturnOrderItem returnOrderItemId);

}
