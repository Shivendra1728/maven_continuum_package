package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.ReturnRoom;

public interface ReturnRoomService {
	public List<ReturnRoom> getAll();
	public List<ReturnRoom> getById(Long returnOrderItemId) ;



}
