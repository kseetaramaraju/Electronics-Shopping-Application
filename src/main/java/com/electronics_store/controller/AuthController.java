package com.electronics_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.electronics_store.requestdto.UserRequest;
import com.electronics_store.responsedto.UserResponse;
import com.electronics_store.service.AuthService;
import com.electronics_store.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> register(@RequestBody @Valid UserRequest userRequest)
	{
		return authService.register(userRequest);
	}

}
