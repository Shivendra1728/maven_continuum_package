package com.di.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SidebarAdminDTO {

	private String name;

	private String type;

	private String icon;

	private String url;

	private boolean isNavigate;
}
