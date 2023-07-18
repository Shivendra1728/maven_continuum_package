package com.continuum.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AzureBlobService {

	String uploadFiles(MultipartFile[] data, String rmaNo) throws IOException;

}
