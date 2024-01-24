package com.di.commons.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class P21ProductItem {
	private String inv_mast_uid;
	private String item_id;
	private String item_desc;
	private String price1;

}
