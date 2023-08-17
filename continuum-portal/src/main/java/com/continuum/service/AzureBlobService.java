package com.continuum.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.continuum.tenant.repos.entity.ReturnOrderItem;

public interface AzureBlobService {

	String uploadFiles(MultipartFile[] data, String rmaNo, ReturnOrderItem returnOrderItemid) throws IOException;

}