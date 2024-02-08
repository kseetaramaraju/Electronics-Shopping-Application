package com.electronics_store.service;

import org.springframework.http.ResponseEntity;

import com.electronics_store.requestdto.OtpModel;
import com.electronics_store.requestdto.UserRequest;
import com.electronics_store.responsedto.UserResponse;
import com.electronics_store.util.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface AuthService {

	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) throws MessagingException;

	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel);

}
