package com.security.advance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Md4PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
		
		String encodedPassword=md5Encoder(user.getPassword());
		user.setEmail(user.getEmail());
		user.setId(user.getId());
		user.setUsername(user.getUsername());
		user.setPassword(encodedPassword);
		return "Saved successfully";
	}
	
	public String md5Encoder(String password) {
		
		PasswordEncoder encoder=new Md4PasswordEncoder();
				String pass =encoder.encode(password);
		return pass;
		
	}

}
