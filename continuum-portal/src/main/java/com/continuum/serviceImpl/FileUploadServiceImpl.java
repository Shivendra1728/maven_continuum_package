package com.continuum.serviceImpl;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.continuum.service.FileUploadService;
import com.continuum.tenant.repos.entity.OrderItemDocuments;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.UserRepository;

@Service
public class FileUploadServiceImpl implements FileUploadService {

//	@Autowired
//	FileUploadHelper fileuploadhelper;
	@Value("${azure.storage.connection-string-value}")
	private String connectionString;
	@Value("${azure.storage.container-name}")
	private String containerName;
	@Autowired
	UserRepository userRepository;

	@Override
	public String fileUploader(MultipartFile data, Long userId) {
		boolean b = false;
		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);
		try {
			Optional<User> optionalUser = userRepository.findById(userId);

			if (optionalUser.isPresent()) {
				User user = optionalUser.get();

				String originalFilename = data.getOriginalFilename();
				String fileExtension = getFileExtension(originalFilename);

				if (isValidFileType(fileExtension)) {
					BlobClient blobClient = containerClient.getBlobClient(userId + "/" + originalFilename);
					InputStream inputStream = data.getInputStream();
					blobClient.upload(inputStream, data.getSize());

					user.setUrl(blobClient.getBlobUrl());
					userRepository.save(user);
					return "user profile added";
				} else {
					return "User not found";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean isValidFileType(String fileExtension) {
		return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png");
	}

	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex > -1 && dotIndex < fileName.length() - 1) {
			return fileName.substring(dotIndex + 1).toLowerCase();
		}
		return "";
	}

}

