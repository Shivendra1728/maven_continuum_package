package com.continuum.serviceImpl;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public List<Map<String, String>> uploadFiles(MultipartFile[] data, String customerId) throws Exception {

		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);

		OffsetDateTime now = OffsetDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		List<Map<String, String>> list = new ArrayList<>();

		for (MultipartFile file : data) {

			Map<String, String> fileUrl = new HashMap<String, String>();
			String fileName = file.getOriginalFilename();
			String fileExtension = getFileExtension(fileName);

			if (isValidFileType(fileExtension)) {

				BlobClient blobClient = containerClient
						.getBlobClient(customerId + formatter.format(now) + "/" + fileName);
				InputStream inputStream = file.getInputStream();
				blobClient.upload(inputStream, file.getSize());
				fileUrl.put("image", fileName);
				fileUrl.put("url", blobClient.getBlobUrl());

				list.add(fileUrl);

			} else {
				throw new Exception("Invalid file type!");
			}
		}
		return list;

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