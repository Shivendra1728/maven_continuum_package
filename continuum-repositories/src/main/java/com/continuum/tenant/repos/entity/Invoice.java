package com.continuum.tenant.repos.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice")
public class Invoice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "inv_no")
	private String invNo;

	@Column(name = "inv_date")
	private String invDate;

	@Column(name = "sono")
	private String soNo;

	@Column(name = "pono")
	private String poNo;

	@Column(name = "currency")
	private String currency;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "contact_phone")
	private String contactPhone;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "sales_loc")
	private String salesLoc;

	@Column(name = "loc_id")
	private String locId;

	@Column(name = "quantity")
	private String qty;

	@Column(name = "item_desc")
	private String itemDesc;

	@Column(name = "part_no")
	private String partNo;

	@Column(name = "brand")
	private String brand;

	@Column(name = "amt")
	private String amt;

	@Column(name = "warehouse_id")
	private String warehouseId;

	@Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean isActive;

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", invNo=" + invNo + ", invDate=" + invDate + ", soNo=" + soNo + ", poNo=" + poNo
				+ ", currency=" + currency + ", customerId=" + customerId + ", contactId=" + contactId
				+ ", contactEmail=" + contactEmail + ", contactPhone=" + contactPhone + ", contactName=" + contactName
				+ ", salesLoc=" + salesLoc + ", locId=" + locId + ", qty=" + qty + ", itemDesc=" + itemDesc
				+ ", partNo=" + partNo + ", brand=" + brand + ", amt=" + amt + ", warehouseId=" + warehouseId
				+ ", isActive=" + isActive + "]";
	}

	// InvNo|InvDate|SONo|PONo|Currency|CustomerID|ContactID|ContactEmail|ContactPhone|ContactName|SalesLoc|LocID|Qty|ItemDesc|PartNo|Brand|Amt|WarehouseID

}