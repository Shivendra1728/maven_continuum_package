package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
class StoreLocale extends BaseEntity {

	private String locale;

	@OneToOne(mappedBy = "storeLocale")
	private Store store;

}
