package com.continuum.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.continuum.service.AzureBlobService;
import com.continuum.tenant.repos.entity.OrderItemDocuments;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.repositories.OrderItemDocumentRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;

@Component
public class AzureBlobStorageServiceImpl implements AzureBlobService {

	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);
	@Value("${azure.storage.connection-string-value}")
	private String connectionString;
	@Value("${azure.storage.container-name}")
	private String containerName;

	@Value("${azure.storage.connection-string-value.g2s}")
	private String connectionStringg2s;
	@Value("${azure.storage.container-name.extracts.g2s}")
	private String containerNameg2s;

	@Autowired
	OrderItemDocumentRepository orderItemDocumentRepository;
	
	@Autowired
    ReturnOrderItemRepository returnOrderItemRepository;

	public List<Map<String, String>> uploadFiles(List<MultipartFile> data, String customerId) throws Exception {

		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);

		OffsetDateTime now = OffsetDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		List<Map<String, String>> list = new ArrayList<>();

		for (MultipartFile file : data) {

			if (file == null || file.isEmpty()) {
				continue; // Skip this file and continue with the next one
			}

			Map<String, String> fileUrl = new HashMap<String, String>();

			String fileName = file.getOriginalFilename();

			String fileExtension = getFileExtension(fileName);

			if (isValidFileType(fileExtension)) {

				BlobClient blobClient = containerClient
						.getBlobClient(customerId + formatter.format(now) + "/" + fileName);

				boolean fileAlreadyExists = false;

				for (Map<String, String> existingMap : list) {
					if (existingMap.containsValue(fileName)) {
						fileAlreadyExists = true;
						break;
					}
				}

				if (!fileAlreadyExists) {
					InputStream inputStream = file.getInputStream();

					blobClient.upload(inputStream, file.getSize());

					fileUrl.put("image", fileName);
					fileUrl.put("url", blobClient.getBlobUrl());

					list.add(fileUrl);

					inputStream.close();

				} else {
					continue;
				}

			} else {

				throw new Exception("Invalid file type!");

			}

		}

		return list;

	}
	
	public List<Map<String, String>> uploadAttachment(List<MultipartFile> data, Long lineItemId) throws Exception {

		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);

		OffsetDateTime now = OffsetDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		List<Map<String, String>> list = new ArrayList<>();
		
	   ReturnOrderItem returnOrderItem = 	returnOrderItemRepository.findById(lineItemId).get();
		

		for (MultipartFile file : data) {

			if (file == null || file.isEmpty()) {
				continue; // Skip this file and continue with the next one
			}
			

			Map<String, String> fileUrl = new HashMap<String, String>();

			String fileName = file.getOriginalFilename();

			String fileExtension = getFileExtension(fileName);

			if (isValidFileType(fileExtension)) {

				BlobClient blobClient = containerClient
						.getBlobClient(lineItemId + formatter.format(now) + "/" + fileName);

				boolean fileAlreadyExists = false;

				for (Map<String, String> existingMap : list) {
					if (existingMap.containsValue(fileName)) {
						fileAlreadyExists = true;
						break;
					}
				}

				if (!fileAlreadyExists) {
					
					OrderItemDocuments orderItemDocument = new OrderItemDocuments(); 
					InputStream inputStream = file.getInputStream();

					blobClient.upload(inputStream, file.getSize());
					
					orderItemDocument.setStatus("note");
					orderItemDocument.setType("note");
					orderItemDocument.setURL(blobClient.getBlobUrl());
					orderItemDocument.setReturnOrderItem(returnOrderItem);
					
					orderItemDocumentRepository.save(orderItemDocument);

					fileUrl.put("image", fileName);
					fileUrl.put("url", blobClient.getBlobUrl());

					list.add(fileUrl);

					inputStream.close();

				} else {
					continue;
				}

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

	private boolean isValidFileCSVType(String fileExtension) {
		return fileExtension.equalsIgnoreCase("csv");
	}

	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex > -1 && dotIndex < fileName.length() - 1) {
			return fileName.substring(dotIndex + 1).toLowerCase();
		}
		return "";
	}

	@Override
	public List<Map<String, String>> uploadCSV(List<MultipartFile> files) throws Exception {
		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionStringg2s)
				.buildClient().getBlobContainerClient(containerNameg2s);

		OffsetDateTime now = OffsetDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		List<Map<String, String>> fileUrls = new ArrayList<>();

		for (MultipartFile file : files) {
			if (file == null || file.isEmpty()) {
				continue; // Skip this file and continue with the next one
			}

			Map<String, String> fileUrl = new HashMap<>();

			String fileName = file.getOriginalFilename();

			String fileExtension = getFileExtension(fileName);

			if (isValidFileCSVType(fileExtension)) {
				// Create a different containerClient for CSV files
				BlobContainerClient csvContainerClient = new BlobServiceClientBuilder()
						.connectionString(connectionStringg2s).buildClient().getBlobContainerClient(containerNameg2s);

				BlobClient blobClient = csvContainerClient.getBlobClient(fileName);

				boolean fileAlreadyExists = false;

				for (Map<String, String> existingMap : fileUrls) {
					if (existingMap.containsValue(fileName)) {
						fileAlreadyExists = true;
						break;
					}
				}

				if (!fileAlreadyExists) {
					InputStream inputStream = file.getInputStream();

					blobClient.upload(inputStream, file.getSize());

					fileUrl.put("file", fileName);
					fileUrl.put("url", blobClient.getBlobUrl());

					fileUrls.add(fileUrl);
					logger.info("File URL is:" + fileUrl);
					inputStream.close();
				} else {
					continue;
				}
			} else {
				throw new Exception("Invalid file type!");
			}
		}

		return fileUrls;
	}
}