package com.di.integration.p21.service;

import com.di.commons.dto.DocumentLinkDTO;

public interface P21DocumentService {

	boolean linkDocument(DocumentLinkDTO documentLinkDTO) throws Exception;

}
