package com.continuum.repos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.continuum.repos.entity.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository  extends JpaRepository<PurchaseOrder, Long>{

	Optional<PurchaseOrder> getOrdersByInvoiceNoAndBillTo_ZipcodeOrShipTo_Zipcode(String invoiceNo,String zipcode,String zipcode1);

	Optional<PurchaseOrder> getOrdersByPONumberAndBillTo_Zipcode(String PONumber,String zipcode);

	Optional<PurchaseOrder> getOrdersByCustomerIdAndPONumber(Long customerId, String poNo);

	Optional<PurchaseOrder> getOrdersByCustomerIdAndInvoiceNo(Long customerId, String invoiceNo);

}
