package com.di.commons.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.continuum.repos.entity.ReturnOrderItem;

@Component
public class FileUploadHelper {

	@Autowired
	OrderItemDocumentsHelper orderItemDocumentsHelper;

	@Value("${file.upload.directory}")
	private String uploadDirectory;

	public FileUploadHelper() throws IOException {
	}

	public boolean fileUploaders(MultipartFile data, String type, ReturnOrderItem returnOrderItemId) {
		File directory = new File(uploadDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		boolean f = false;
		try {
			String originalFilename = data.getOriginalFilename();
			String fileExtension = getFileExtension(originalFilename);

			if (isValidFileType(fileExtension)) {
				Files.copy(data.getInputStream(), Paths.get(uploadDirectory + "/" + originalFilename),
						StandardCopyOption.REPLACE_EXISTING);

				orderItemDocumentsHelper.storeOrderItemDocument(uploadDirectory, type, returnOrderItemId);

				f = true;
			} else {
				f = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
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
