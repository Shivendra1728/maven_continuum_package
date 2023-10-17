package com.di.integration.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
//import com.continuum.serviceImpl.AzureBlobDownloader;
import com.continuum.tenant.repos.entity.Invoice;
import com.di.integration.p21.serviceImpl.AzureBlobDownloader;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@EnableJpaRepositories
@Slf4j
public class BatchConfig {

	@Autowired
	public DataSource dataSource;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	AzureBlobDownloader azureBlobDownloader;

	@Value("${azure.storage.connection-string-value.g2s}")
	private String connectionString;
	@Value("${azure.storage.container-name.extracts.g2s}")
	private String containerName;

	private String ProcessedFileName = null;

	@Bean
	@Scope("prototype")
	public FlatFileItemReader<Invoice> reader() {

		log.info("Reader is Runninng...........!");

		BlobContainerClient containerClient = getBlobContainerClient(containerName);

		BlobItem blobItem = containerClient.listBlobs().stream().findFirst().orElse(null);

		if (blobItem != null) {
			BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
			ProcessedFileName = blobItem.getName();

			log.info("Processed file name :" + ProcessedFileName);

			if (blobClient.exists()) {

				// Open an InputStream to the blob content
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				blobClient.download(outputStream);

				// converting it to the inputStream to return
				final byte[] bytes = outputStream.toByteArray();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
				ByteArrayResource resource = new ByteArrayResource(bytes);

				FlatFileItemReader<Invoice> reader = new FlatFileItemReader<>();
				reader.setResource(resource);
				reader.setLineMapper(getLineMapper());
				reader.setLinesToSkip(1);

				return reader;
			} else {
				log.error("File does not exist in Azure Blob Storage: " + ProcessedFileName);
			}
		} else {

			log.error("No files found in Azure Blob Storage container: " + containerName);
			FlatFileItemReader<Invoice> reader = new FlatFileItemReader<>();
			reader.setResource(new ClassPathResource("/Empty.csv"));
			reader.setLineMapper(getLineMapper());
			reader.setLinesToSkip(1);

			return reader;

		}

		return null;

	}

	private LineMapper<Invoice> getLineMapper() {

		log.info("Line Mapper is Runninng...........!");

		DefaultLineMapper<Invoice> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter("|");
		lineTokenizer.setNames(new String[] { "invNo", "InvDate", "SONo", "PONo", "Currency", "CustomerID", "ContactID",
				"ContactEmail", "ContactPhone", "ContactName", "SalesLoc", "LocID", "Qty", "ItemDesc", "PartNo",
				"Brand", "Amt", "WarehouseID" });
		lineTokenizer.setIncludedFields(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 });
		BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
		fieldSetMapper.setTargetType(Invoice.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	@Scope("prototype")
	public CsvItemProcessor processor() {
		log.info("processor is Runninng...........!");
		return new CsvItemProcessor();
	}

	@Bean
	@Scope("prototype")
	public JdbcBatchItemWriter<Invoice> writer() {

		log.info("Writer is Runninng...........!");

		JdbcBatchItemWriter<Invoice> writer = new JdbcBatchItemWriter();
		writer.setDataSource(dataSource);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Invoice>());
		writer.setSql("insert into "
				+ "invoice(inv_no, inv_date, sono, pono, currency, customer_id, contact_id, contact_email, contact_phone, contact_name, sales_loc, loc_id, quantity, item_desc, part_no, brand, amt, warehouse_id)"
				+ " values(:invNo, :invDate, :soNo, :poNo, :currency, :customerId, :contactId, :contactEmail, :contactPhone, :contactName, :salesLoc, :locId, :qty, :itemDesc, :partNo, :brand, :amt, :warehouseId)");

		return writer;
	}

	@Bean
	@Scope("prototype")
	public Job importCsvJob() {
		log.info("importCsvJob is Runninng...........!");
		return this.jobBuilderFactory.get("CSV-IMPORT-JOB").incrementer(new RunIdIncrementer()).flow(step1()).end()
				.build();

	}

	@Bean
	@Scope("prototype")
	public Step step1() {

		try {

			return this.stepBuilderFactory.get("step1").<Invoice, Invoice>chunk(10).reader(reader())
					.processor(processor()).writer(writer()).build();
		} catch (Exception e) {

			log.info("File data is not proper");
			return null;

		}

	}

	// @Scheduled(fixedRate = 30000) // Run every 30 seconds (adjust the rate as
	// needed)
	public void runBatchJob() throws IOException {

		log.info("Batch JOB is Runninng...........!");

		try {

			BlobContainerClient sourceContainer = getBlobContainerClient(containerName);
			BlobContainerClient destinationContainer = getBlobContainerClient("processed");

			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();

			JobExecution jobExecution = jobLauncher.run(importCsvJob(), jobParameters);
			log.info("Batch Job completed with status: " + jobExecution.getStatus());

			if (jobExecution.getStatus() == BatchStatus.FAILED) {

				throw new JobExecutionException("Batch job failed...!!!");
			}

			if (ProcessedFileName == null) {
				ProcessedFileName = readFileName();

			}

			BlobClient blobClient = sourceContainer.getBlobClient(ProcessedFileName);

			destinationContainer.getBlobClient(ProcessedFileName).beginCopy(blobClient.getBlobUrl(), null);
			log.info("file uploaded in processed file......!!!!");

			blobClient.deleteIfExists();

			ProcessedFileName = null;

		} catch (JobExecutionException e) {
			log.error("Batch Job failed", e);

			BlobContainerClient sourceContainer = getBlobContainerClient(containerName);
			BlobClient blobClient = sourceContainer.getBlobClient(ProcessedFileName);

			BlobContainerClient errorContainer = getBlobContainerClient("errors");

			errorContainer.getBlobClient(ProcessedFileName).beginCopy(blobClient.getBlobUrl(), null);
			log.info("file moved to error container......!!!!");

			blobClient.deleteIfExists();
			log.info("file deleted from extracts container......!!!!!!!");
			log.error("Batch Job failed for file: " + ProcessedFileName);

			ProcessedFileName = null;

		}
	}

	public String readFileName() {
		BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient().getBlobContainerClient(containerName);

		BlobItem blobItem = containerClient.listBlobs().stream().findFirst().orElse(null);

		if (blobItem != null) {
			BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
			ProcessedFileName = blobItem.getName();
			return ProcessedFileName;
		}

		return null;
	}

	private BlobContainerClient getBlobContainerClient(String containerName) {
		return new BlobServiceClientBuilder().connectionString(connectionString).buildClient()
				.getBlobContainerClient(containerName);
	}

}
