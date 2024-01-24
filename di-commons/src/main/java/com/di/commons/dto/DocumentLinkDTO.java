package com.di.commons.dto;

import java.util.List;

import com.di.commons.helper.DocumentLinkHelper;

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
public class DocumentLinkDTO {

	private String rmaNo;
	private List<DocumentLinkHelper> documentLinkHelperList;
}
