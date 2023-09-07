package com.di.commons.dto;

import java.util.List;

import javax.persistence.Column;

import com.continuum.tenant.repos.entity.SidebarAdmin;
import com.continuum.tenant.repos.entity.SidebarRMA;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class sidebarRMADTO {
	private String name;
    
	private String type;
    
	private String icon;
    
	private String url;
    
	private boolean isNavigate;

}
