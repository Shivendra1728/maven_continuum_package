package com.continuum.serviceImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.continuum.service.AzureBlobService;
import com.continuum.tenant.repos.repositories.OrderItemDocumentRepository;

@Component

public class AzureBlobStorageServiceImpl implements AzureBlobService {

	@Value("${azure.storage.connection-string-value}")
	private String connectionString;
	@Value("${azure.storage.container-name}")
	private String containerName;
	@Autowired
	OrderItemDocumentRepository orderItemDocumentRepository;

	public List<String> uploadFiles(MultipartFile[] data, String customerId) throws Exception {

		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
											 .buildClient().getBlobContainerClient(containerName);
		List<String> url = new ArrayList<String>();
		for (MultipartFile file : data) {
			String fileName = file.getOriginalFilename();
			String fileExtension = getFileExtension(fileName);

			if (isValidFileType(fileExtension)) {

				BlobClient blobClient = containerClient.getBlobClient(customerId + "/" + fileName);
				InputStream inputStream = file.getInputStream();
				blobClient.upload(inputStream, file.getSize());
				url.add(blobClient.getBlobUrl());

			} else {
				throw new Exception("Invalid file type!");
			}
		}
		return url;

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