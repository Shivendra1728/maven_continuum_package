package com.di.commons.dto;

import java.util.ArrayList;
import java.util.List;

import com.continuum.tenant.repos.entity.Page;
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
public class PageDTO {
	
	public PageDTO(Page e) {
		// TODO Auto-generated constructor stub
	}

    List<SidebarAdmin> admin=new ArrayList<>();
    
    List<SidebarRMA> RMA = new ArrayList<>();

}
