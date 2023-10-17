package com.continuum.scheduler.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.di.integration.config.BatchConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class Scheduler {

	@Value("${azure.storage.connection-string-value.g2s}")
	private String connectionString;

	@Value("${azure.storage.container-name.extracts.g2s}")
	private String containerName;

	@Autowired
	private BatchConfig batchConfig;

	@Scheduled(fixedRate = 30000) // Run every minute, adjust as needed
	public void csvToMysqlJob() throws Exception {

		log.info("CSV loading Scheduler Running...!!!");

		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);

		log.info("list of blob names");

		int initialRunCount = (int) containerClient.listBlobs().stream().count();

		while (initialRunCount > 0) {

			batchConfig.runBatchJob();

			initialRunCount--;
		}
	}

}
