package com.continuum.serviceImpl;

import java.io.IOException;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;

import com.azure.storage.blob.BlobContainerClient;

import com.azure.storage.blob.BlobServiceClientBuilder;

import com.di.integration.p21.transaction.P21RMAResponse;

@Component

public class AzureBlobStorageService {

	@Value("${azure.storage.connection-string-value}")
	private String connectionString;

	@Value("${azure.storage.container-name}")
	private String containerName;

	// P21RMAResponse p21rmaResponse;

	public String uploadFiles(MultipartFile[] data, String rmaNo) throws IOException {

		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);

		for (MultipartFile file : data) {

			String fileName = file.getOriginalFilename();
			String fileExtension = getFileExtension(fileName);

			if (!isValidFileType(fileExtension)) {
				BlobClient blobClient = containerClient.getBlobClient(rmaNo + "/" + fileName);
				InputStream inputStream = file.getInputStream();
				blobClient.upload(inputStream, file.getSize());
			} else {
				return "invalid file type !";
			}
		}
		return "";
	}

	private boolean isValidFileType(String fileExtension) {
		return fileExtension.equals("pdf") || fileExtension.equals("doc") || fileExtension.equals("jpg")
				|| fileExtension.equals("jpeg") || fileExtension.equals("png");
	}

	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex > -1 && dotIndex < fileName.length() - 1) {
			return fileName.substring(dotIndex + 1).toLowerCase();
		}
		return "";
	}

}
