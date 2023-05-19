package com.di.commons.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;

//import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadHelper {
    /* add the path of the folder which is present in static folder */
	public final String UPLOAD_Dir = 
			"C:\\Users\\ADMIN\\Desktop\\Continuum-Backend\\continuum-portal\\src\\main\\resources\\static\\Doc";
     
	//public final String UPLOAD_Dir=new ClassPathResource("/static/Doc/").getFile().getAbsolutePath(); 
	
	public FileUploadHelper() throws IOException{
		
	}

	public boolean fileUploader(MultipartFile data) {
		boolean f = false;
		try {
       
			InputStream is = data.getInputStream();
			byte b[] = new byte[is.available()];
			is.read(b);

			FileOutputStream fs = new FileOutputStream(UPLOAD_Dir + "\\" + data.getOriginalFilename());
			fs.write(b);

			fs.close();
			is.close();
            /*
			Files.copy(data.getInputStream(),Paths.get(UPLOAD_Dir+"\\"+data.getOriginalFilename()),StandardCopyOption.COPY_ATTRIBUTES);
			*/
			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
