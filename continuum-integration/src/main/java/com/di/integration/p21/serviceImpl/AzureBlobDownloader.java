package com.di.integration.p21.serviceImpl;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Component
public class AzureBlobDownloader {

	@Value("${azure.storage.connection-string-value.g2s}")
	private String connectionString;
    @Value("${azure.storage.container-name.extracts.g2s}")
	private String containerName;

	public void downloadCsvFileToLocal() throws IOException{
		 
		 
			BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
					.buildClient().getBlobContainerClient(containerName);

			BlobClient blobClient = containerClient.getBlobClient(containerName);

			if(blobClient.exists())
			{
				File localFile = new File("C://Users/Lenovo/Downloads/");
				
				blobClient.downloadToFile(localFile.toPath().toString(), true);
			}
			else {
	            throw new IOException("Blob not found");
	        } 
		 
	 }

}
