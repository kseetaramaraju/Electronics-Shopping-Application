package com.electronics_store.serviceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.electronics_store.entity.User;
import com.electronics_store.requestdto.UserRequest;
import com.electronics_store.responsedto.UserResponse;
import com.electronics_store.service.AuthService;
import com.electronics_store.util.ResponseStructure;

import jakarta.validation.Valid;

@Service
public class AuthServiceImpl implements AuthService{
	
//	public <T extends User> T mapToUser(UserRequest userRequest)
//	{
//		return 
//	}
//	

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(@Valid UserRequest userRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
