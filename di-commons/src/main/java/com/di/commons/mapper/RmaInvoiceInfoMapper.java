package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.di.commons.dto.RmaInvoiceInfoDTO;

@Component
public class RmaInvoiceInfoMapper {

	@Autowired
	ModelMapper modelMapper;

	public RmaInvoiceInfoDTO RmainvoiceInfotoDTO(RmaInvoiceInfo rmaInvoiceInfo) {

		RmaInvoiceInfoDTO rmaInvoiceInfoDTO = modelMapper.map(rmaInvoiceInfo, RmaInvoiceInfoDTO.class);
		return rmaInvoiceInfoDTO;
	}

	public RmaInvoiceInfo RmaInvoiceInfoToRmaInvoiceInfoDTO(RmaInvoiceInfoDTO rmaInvoiceInfoDTO) {
		RmaInvoiceInfo rmaInvoiceInfo = modelMapper.map(rmaInvoiceInfoDTO, RmaInvoiceInfo.class);
		return rmaInvoiceInfo;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());

	}

}
