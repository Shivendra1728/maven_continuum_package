package com.di.integration.p21.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.integration.p21.service.P21ShipInfoService;

@RestController
@RequestMapping("/P21/shipInfo")
public class P21ShipInfoController {

	@Autowired
	P21ShipInfoService p21ShipInfoService;

	@GetMapping("/search")
	public Map<String, String> getShipInfoFromOrderNo(@RequestParam(required = true) String orderNo) throws Exception {
		return p21ShipInfoService.getShipInfo(orderNo, null);

	}

}
