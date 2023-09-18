package com.continuum.scheduler.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
//import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.continuum.tenant.repos.repositories.RmaInvoiceInfoRepository;
import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.di.commons.helper.DBContextHolder;
import com.di.commons.mapper.RmaInvoiceInfoMapper;
import com.di.integration.p21.service.P21InvoiceService;

@Component
public class P21InvoiceLinkTaskScheduler {
	private static final Logger logger = LoggerFactory.getLogger(P21InvoiceLinkTaskScheduler.class);
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
	@Autowired
	MasterTenantRepository masterTenantRepo;

	@Scheduled(cron = "*/10 * * * * *")
	public void runTasks() throws Exception{
		 List<MasterTenant> masterTenants = masterTenantRepo.findAll();
        if(null == masterTenants){
            logger.error("An error during getting tenant name");
            throw new BadCredentialsException("Invalid tenant and user.");
        }
        for (MasterTenant masterTenant : masterTenants) {
       	  DBContextHolder.setCurrentDb(masterTenant.getDbName());
       	  linkInvoice();
		}
       
	}
	
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
