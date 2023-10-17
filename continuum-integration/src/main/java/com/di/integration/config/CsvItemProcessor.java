package com.di.integration.config;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Invoice;
import com.continuum.tenant.repos.repositories.CsvRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CsvItemProcessor implements ItemProcessor<Invoice, Invoice> {

	@Autowired
	private CsvRepository csvRepository;

	@Override
	public Invoice process(Invoice item) throws Exception {

		log.info("ITEM:!!!!!!!" + item + "");

		item.setIsActive(true);

		List<Invoice> recordExists = csvRepository.findByInvNo(item.getInvNo());

		for (Invoice csvItem : recordExists) {
			if (csvItem != null && csvItem.getInvDate().equals(item.getInvDate())
					&& csvItem.getSoNo().equals(item.getSoNo()) && csvItem.getPartNo().equals(item.getPartNo())) {
				log.info("csvItem is null!!!!!");
				return null;
			} else if (csvItem.getInvDate().equals(item.getInvDate())) {

				csvItem.setIsActive(false);
				csvRepository.save(csvItem);
				item.setIsActive(true);
			}
		}

		return item; // Process this record
	}

}
