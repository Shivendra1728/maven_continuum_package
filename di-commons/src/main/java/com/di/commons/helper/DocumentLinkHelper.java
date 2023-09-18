package com.di.commons.helper;

import java.util.Date;

import com.di.commons.dto.OrderAddressDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class DocumentLinkHelper {

	private String linkName;
	private String linkPath;
}
