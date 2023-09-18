package com.di.integration.p21.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.DocumentLinkDTO;
import com.di.integration.p21.service.P21DocumentService;

@RestController
@RequestMapping("/P21/document")
public class P21DocumentController {
	@Autowired
	P21DocumentService documentService;

	@PostMapping("/link")
	public void linkInvoice(@RequestBody DocumentLinkDTO documentLinkDTO) throws URISyntaxException, Exception {
		documentService.linkDocument(documentLinkDTO);
	}
}