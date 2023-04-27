package com.security.advance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.security.advance.model.User;
import com.security.advance.repository.UserRepository;
import com.security.advance.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{
	
	@Autowired
	UserRepository userRepo;

	@Override
	public String addData(User user) {
		userRepo.save(user);
		return "Saved successfully";
	}

}
