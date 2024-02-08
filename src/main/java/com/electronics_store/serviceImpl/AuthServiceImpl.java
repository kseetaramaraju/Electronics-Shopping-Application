package com.electronics_store.serviceImpl;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.electronics_store.entity.Customer;
import com.electronics_store.entity.Seller;
import com.electronics_store.entity.User;
import com.electronics_store.enums.UserRole;
import com.electronics_store.exception.EmailAlreadyExist;
import com.electronics_store.exception.IllegelUserRoleException;
import com.electronics_store.repository.CustomerRepository;
import com.electronics_store.repository.SellerRepository;
import com.electronics_store.repository.UserRepository;
import com.electronics_store.requestdto.UserRequest;
import com.electronics_store.responsedto.UserResponse;
import com.electronics_store.service.AuthService;
import com.electronics_store.util.ResponseEntityProxy;
import com.electronics_store.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

	private UserRepository userRepo;
	private CustomerRepository customerRepo;
	private SellerRepository sellerRepo;
	private ResponseStructure<UserResponse> structure;
	private PasswordEncoder passwordEncoder;

	public <T extends User> T mapToRespective(UserRequest userRequest)
	{
		User user=null;
		
		switch ( UserRole.valueOf(userRequest.getUserRole().toUpperCase()) ) {
		case SELLER -> {user = new Seller();}
		case CUSTOMER -> {user = new Customer();}
		}

		user.setUserName(userRequest.getUserEmail().split("@")[0].toString());
		user.setUserEmail(userRequest.getUserEmail());
		user.setUserPassword(passwordEncoder.encode(userRequest.getUserPassword()));
		user.setUserRole(UserRole.valueOf(userRequest.getUserRole().toUpperCase()));
		user.setDeleted(false);
		user.setEmailVerified(false);

		return (T)user;
	}

	public UserResponse mapToUserResponse(User user)
	{
		return UserResponse.builder()
				.userId(user.getUserId())
				.userName(user.getUserName())
				.userEmail(user.getUserEmail())
				.userRole(user.getUserRole())
				.isDeleted(user.isDeleted())
				.isEmailVerified(user.isEmailVerified())
				.build();
	}






	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register( UserRequest userRequest) {

		
			User user=userRepo.findByUserName(userRequest.getUserEmail().split("@")[0]).map(u -> {
				if(u.isEmailVerified())
				{
				   throw new EmailAlreadyExist("User Already Exists with specified email id!!");
				}
				return u;
			})
			.orElseGet(()->saveUser(userRequest));

			

			return ResponseEntityProxy.setResponseStructure(HttpStatus.ACCEPTED,
					"Please Verify through OTP sent on your Email Id!!"
					,mapToUserResponse(user));
			
	}

	private User saveUser(UserRequest userRequest)
	{
		User user=null;
		
		switch ( UserRole.valueOf(userRequest.getUserRole().toUpperCase()) ) {
		
		case SELLER -> {user=sellerRepo.save(mapToRespective(userRequest));}
		case CUSTOMER -> {user=customerRepo.save(mapToRespective(userRequest));}
		default-> throw new IllegelUserRoleException("Illegal UserRole!!");
		}
		return user;
		
	}


}
