package com.di.integration.p21.service;

import org.springframework.web.bind.annotation.RequestParam;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.di.commons.dto.DocumentLinkDTO;

public interface P21DocumentService {

	boolean linkDocument(DocumentLinkDTO documentLinkDTO,@RequestParam(required = false) MasterTenant masterTenant) throws Exception;

}
