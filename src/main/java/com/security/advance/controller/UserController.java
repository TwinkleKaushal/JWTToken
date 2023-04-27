package com.security.advance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.advance.common.UserDto;
import com.security.advance.model.User;
import com.security.advance.service.IUserService;
import com.security.advance.util.JwtUtil;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	IUserService userService;
	
	@Autowired
	JwtUtil jwtutil;
	
	@Autowired
	AuthenticationManager manager;
	
	@PostMapping("/add")
	public ResponseEntity<?> saveData(@RequestBody User user){
		return new ResponseEntity<>(userService.addData(user),HttpStatus.OK);
		
	}
	
	@PostMapping("/token")
	public String generateToken(@RequestBody UserDto userDto) throws Exception {
		
		try {
		//valide Username password if autheticate then generate token
		manager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
		}
		catch (Exception e) {
			throw new Exception("Inavlid username/password");
		}
		return jwtutil.generateToken(userDto.getUsername());
	}
	
	@PostMapping("/welcome")
	public String welcome() {
		return "WELCOME";
	}

}
