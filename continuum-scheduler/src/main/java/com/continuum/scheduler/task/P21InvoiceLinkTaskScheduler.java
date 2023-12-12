package com.continuum.scheduler.task;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.OrderItemDocuments;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.continuum.tenant.repos.repositories.OrderItemDocumentRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
//import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.continuum.tenant.repos.repositories.RmaInvoiceInfoRepository;
import com.di.commons.dto.DocumentLinkDTO;
import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.di.commons.helper.DBContextHolder;
import com.di.commons.helper.DocumentLinkHelper;
import com.di.commons.mapper.RmaInvoiceInfoMapper;
import com.di.integration.p21.service.P21DocumentService;
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
	@Autowired
	OrderItemDocumentRepository orderItemDocumentRepository;
	@Autowired
	P21DocumentService p21DocumentService;


	@Scheduled(cron = "*/30 * * * * *")
	public void runTasks() throws Exception {
		List<MasterTenant> masterTenants = masterTenantRepo.findAll();
		if (null == masterTenants) {
			logger.error("An error during getting tenant name");
			throw new BadCredentialsException("Invalid tenant and user.");
		}
		for (MasterTenant masterTenant : masterTenants) {
			DBContextHolder.setCurrentDb(masterTenant.getDbName());
			linkInvoice(masterTenant);
		}

	}

	public void linkInvoice(MasterTenant masterTenant) throws Exception {
		logger.info("In  document linking started");
		List<RmaInvoiceInfo> rma = rmaInvoiceInfoRepository.findAll();

		List<RmaInvoiceInfoDTO> rmaDTOList = new ArrayList<>();

		for (RmaInvoiceInfo rmaInvoiceInfo : rma) {
			try {
				if (!rmaInvoiceInfo.isDocumentLinked() && rmaInvoiceInfo.getRetryCount() < 3) {
					linkDocuments(rmaInvoiceInfo,masterTenant);
					rmaInvoiceInfo.setDocumentLinked(true);
					Optional<ReturnOrder> roo = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());
					if (roo.isPresent()) {
						ReturnOrder RO = roo.get();
						RO.setISDocumentLinked(true);
						returnOrderRepository.save(RO);
					}
				}

			} catch (Exception e) {
				logger.error("error in document linking");
				rmaInvoiceInfo.setDocumentLinked(false);
			}
			RmaInvoiceInfoDTO rmaInvoiceInfoDTO = new RmaInvoiceInfoDTO();

			// Manually set properties of rmaInvoiceInfoDTO based on rmaInvoiceInfo
			rmaInvoiceInfoDTO.setRmaOrderNo(rmaInvoiceInfo.getRmaOrderNo());
			rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfo.getRetryCount());
			// Set other properties as needed

			rmaDTOList.add(rmaInvoiceInfoDTO);

			String rmaOrderNo = rmaInvoiceInfoDTO.getRmaOrderNo();
			Integer retryCount = rmaInvoiceInfoDTO.getRetryCount();
			if (retryCount < 3) {
				logger.info("In  document linking started");
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

	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

	private boolean linkDocuments(RmaInvoiceInfo rmaInvoiceInfo,MasterTenant masterTenant) throws Exception {
		boolean docLinked = false;

		Optional<ReturnOrder> ro = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());
		ReturnOrder returnOrder = ro.get();

		List<ReturnOrderItem> returnOrderItemList = returnOrder.getReturnOrderItem();
		DocumentLinkDTO docmentLinkDto = new DocumentLinkDTO();
		docmentLinkDto.setRmaNo(rmaInvoiceInfo.getRmaOrderNo());
		List<DocumentLinkHelper> documentList = new ArrayList();
		for (ReturnOrderItem returnOrderItem : returnOrderItemList) {
			List<OrderItemDocuments> orderItemDocuments = orderItemDocumentRepository
					.findByReturnOrderItem_Id(returnOrderItem.getId());

			for (OrderItemDocuments orderItemDocument : orderItemDocuments) {
				if (isNotNullAndNotEmpty(orderItemDocument.getURL())) {
					String fileName = Paths.get(new URI(orderItemDocument.getURL()).getPath()).getFileName().toString();
					documentList.add(new DocumentLinkHelper(fileName, orderItemDocument.getURL()));
				}

			}
		}
		docmentLinkDto.setDocumentLinkHelperList(documentList);
		p21DocumentService.linkDocument(docmentLinkDto,masterTenant);

		docLinked = true;
		return docLinked;

	}
}
