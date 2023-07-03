package com.di.commons.mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.continuum.repos.entity.Orders;
import com.continuum.repos.entity.ReasonCode;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReasonCodeDTO;

@Component
public class ReasonCodeMapper {
	
	@Autowired
	private ModelMapper modelMapper;
	public ReasonCodeDTO reasonCodeToReasonCodeDTO(ReasonCode rc) {

		ReasonCodeDTO rcDTO = modelMapper.map(rc, ReasonCodeDTO.class);
		if(rc.getParentReasonCode()!=null) {
			rcDTO.setParentReasonCodeId(rc.getParentReasonCode().getId());
		}
		return rcDTO;
	}
	public ReasonCode reasonCodeDTOToReasonCode(ReasonCodeDTO rcDTO) {
		ReasonCode rc = modelMapper.map(rcDTO, ReasonCode.class);
		return rc;
	}


}
