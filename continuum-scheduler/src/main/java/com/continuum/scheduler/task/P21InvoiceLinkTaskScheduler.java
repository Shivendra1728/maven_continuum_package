package com.continuum.scheduler.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
//import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.continuum.tenant.repos.repositories.RmaInvoiceInfoRepository;
import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.di.commons.mapper.RmaInvoiceInfoMapper;
import com.di.integration.p21.service.P21InvoiceService;

import com.di.integration.p21.service.P21InvoiceService;

@Component
public class P21InvoiceLinkTaskScheduler {
//	
//	@Autowired
//	P21InvoiceService p21InvoiceService;
//	
	@Autowired
	ReturnOrderRepository returnOrderRepository;
	@Autowired
	RmaInvoiceInfoRepository rmaInvoiceInfoRepository;
	@Autowired
	P21InvoiceService p21InvoiceService;
	@Autowired
	RmaInvoiceInfoMapper rmaInvoiceInfoMapper;

	@Scheduled(cron = "*/10 * * * * *")
	public void linkInvoice() throws Exception {
		System.out.println("Hello " + LocalDateTime.now());
		List<RmaInvoiceInfo> rma = rmaInvoiceInfoRepository.findAll();

		List<RmaInvoiceInfoDTO> rmaDTOList = new ArrayList<>();

		for (RmaInvoiceInfo rmaInvoiceInfo : rma) {
			RmaInvoiceInfoDTO rmaInvoiceInfoDTO = new RmaInvoiceInfoDTO();

			// Manually set properties of rmaInvoiceInfoDTO based on rmaInvoiceInfo
			rmaInvoiceInfoDTO.setRmaOrderNo(rmaInvoiceInfo.getRmaOrderNo());
			rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfo.getRetryCount());
			// Set other properties as needed

			rmaDTOList.add(rmaInvoiceInfoDTO);

			String rmaOrderNo = rmaInvoiceInfoDTO.getRmaOrderNo();
			Integer retryCount = rmaInvoiceInfoDTO.getRetryCount();
			if (retryCount < 3) {
				boolean bln = p21InvoiceService.linkInvoice(rmaOrderNo);
				if (bln) {
					Optional<ReturnOrder> ro = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());

					if (ro.isPresent()) {

						ReturnOrder returnOrder = ro.get();
						returnOrder.setISInvoiceLinked(true);
						returnOrderRepository.save(returnOrder);

						rmaInvoiceInfoRepository.delete(rmaInvoiceInfo);
						// Here you can remove the corresponding DTO from the list

					}
				} else {
					rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfoDTO.getRetryCount() + 1);
					rmaInvoiceInfoDTO.setInvoiceLinked(false);

					RmaInvoiceInfo rii = rmaInvoiceInfoMapper.RmaInvoiceInfoToRmaInvoiceInfoDTO(rmaInvoiceInfoDTO);
					rmaInvoiceInfoRepository.save(rii);
				}
			}
		}
		System.out.println("Link Invoice API Ran");
		;

	}
}
