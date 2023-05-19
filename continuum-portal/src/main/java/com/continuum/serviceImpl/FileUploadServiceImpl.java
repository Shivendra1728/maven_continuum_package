package com.continuum.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.continuum.service.FileUploadService;
import com.di.commons.helper.FileUploadHelper;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Autowired(required = true)
	private FileUploadHelper fileuploadhelper;

	@Override
	public String fileUploader(MultipartFile data) {
		try {
			boolean b = fileuploadhelper.fileUploader(data);
			if (b) {
				System.out.println("ok,file Sucessfully added");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "File upload Sucessfull";
	}
}
