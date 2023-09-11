package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.ReturnRoomService;
import com.continuum.tenant.repos.entity.ReturnRoom;
import com.continuum.tenant.repos.repositories.ReturnRoomRepository;

@Service
public class ReturnRoomServiceImpl implements ReturnRoomService{
	
	@Autowired
	ReturnRoomRepository returnRoomRepository;

	@Override
	public List<ReturnRoom> getAll() {
		List<ReturnRoom> returnRooms = returnRoomRepository.findAll();
		return returnRooms;
	}

}
