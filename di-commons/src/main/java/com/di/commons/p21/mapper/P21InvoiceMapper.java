package com.di.commons.p21.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.di.commons.dto.OrderAddressDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.P21InvoiceData;
import com.di.commons.helper.P21InvoiceDataHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class P21InvoiceMapper {
    @Autowired
    private final ObjectMapper objectMapper;

    public P21InvoiceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<OrderItemDTO> mapP21InvoiceResponseToData(String responseBody)
            throws JsonMappingException, JsonProcessingException {
        // Parse the JSON response into a P21InvoiceDataHelper object
        P21InvoiceDataHelper p21InvoiceDataHelper = objectMapper.readValue(responseBody, P21InvoiceDataHelper.class);

        List<OrderItemDTO> invoiceItemDTOList = new ArrayList<>();
        
        for (P21InvoiceData p21InvoiceData : p21InvoiceDataHelper.getValue()) {
            OrderItemDTO orderItemDTO = new OrderItemDTO(); // Create a new OrderDTO for each P21InvoiceData
            orderItemDTO.setInvoiceNo(p21InvoiceData.getInvoice_no());
            orderItemDTO.setOrderNo(p21InvoiceData.getOrder_no());
            orderItemDTO.setBillTo(new OrderAddressDTO());
            orderItemDTO.getBillTo().setZipcode(p21InvoiceData.getBill2_postal_code());
            orderItemDTO.setInvoiceDate(p21InvoiceData.getInvoice_date());
            
            invoiceItemDTOList.add(orderItemDTO);
        }

        return invoiceItemDTOList;
    }
}
