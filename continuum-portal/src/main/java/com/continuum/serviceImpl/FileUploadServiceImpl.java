package com.continuum.serviceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.continuum.repos.entity.ReturnOrderItem;
import com.continuum.service.FileUploadService;
import com.di.commons.helper.FileUploadHelper;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Autowired
	FileUploadHelper fileuploadhelper;

	@Override
	public String fileUploader(MultipartFile data, ReturnOrderItem returnOrderItemId) {
		try {
			boolean b = fileuploadhelper.fileUploaders(data, data.getContentType(), returnOrderItemId);
			if (b) {
				return "File upload Sucessfull";
			} else {
				return "Invalid file type. Only PDF, DOC, JPG,JPEG and PNG files are allowed.";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return " ";
	}

}
