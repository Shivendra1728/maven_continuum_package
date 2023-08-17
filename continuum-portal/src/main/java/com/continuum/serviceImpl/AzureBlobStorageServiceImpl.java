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
import com.continuum.tenant.repos.entity.*;
import com.continuum.tenant.repos.repositories.OrderItemDocumentRepository;
import com.continuum.service.AzureBlobService;

@Component

public class AzureBlobStorageServiceImpl implements AzureBlobService {

	@Value("${azure.storage.connection-string-value}")
	private String connectionString;
	@Value("${azure.storage.container-name}")
	private String containerName;
	@Autowired
	OrderItemDocumentRepository orderItemDocumentRepository;

	public String uploadFiles(MultipartFile[] data, String rmaNo, ReturnOrderItem returnOrderItemid)
			throws IOException {
		boolean b = false;
		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);
		

		for (MultipartFile file : data) {

			String fileName = file.getOriginalFilename();
			String fileExtension = getFileExtension(fileName);

			if (isValidFileType(fileExtension)) {
				BlobClient blobClient = containerClient.getBlobClient(rmaNo + "/" + fileName);
				InputStream inputStream = file.getInputStream();
				blobClient.upload(inputStream, file.getSize());
				b = true;
				if (b) {
					OrderItemDocuments orderItemDocument = new OrderItemDocuments();
					orderItemDocument.setURL(blobClient.getBlobUrl());
					orderItemDocument.setType(fileExtension);
					orderItemDocument.setReturnOrderItem(returnOrderItemid);
					// Set other attributes of the OrderItemDocument if needed
					orderItemDocumentRepository.save(orderItemDocument);
				}
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
