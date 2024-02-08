package com.electronics_store.serviceImpl;


import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.electronics_store.cache.CacheBeanConfig;
import com.electronics_store.cache.CacheStore;
import com.electronics_store.entity.Customer;
import com.electronics_store.entity.Seller;
import com.electronics_store.entity.User;
import com.electronics_store.enums.UserRole;
import com.electronics_store.exception.EmailAlreadyExist;
import com.electronics_store.exception.IllegelUserRoleException;
import com.electronics_store.exception.InvalidOTPException;
import com.electronics_store.exception.OtpExpiredException;
import com.electronics_store.exception.RegistrationSessionExpiredException;
import com.electronics_store.repository.CustomerRepository;
import com.electronics_store.repository.SellerRepository;
import com.electronics_store.repository.UserRepository;
import com.electronics_store.requestdto.OtpModel;
import com.electronics_store.requestdto.UserRequest;
import com.electronics_store.responsedto.UserResponse;
import com.electronics_store.service.AuthService;
import com.electronics_store.util.MessageStructure;
import com.electronics_store.util.ResponseEntityProxy;
import com.electronics_store.util.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

	private UserRepository userRepo;
	private CustomerRepository customerRepo;
	private SellerRepository sellerRepo;
	private ResponseStructure<UserResponse> structure;
	private PasswordEncoder passwordEncoder;
	private CacheStore<String> otpcachestore;
	private CacheStore<User> usercachestore;
	private JavaMailSender javaMailSender;


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
	public ResponseEntity<ResponseStructure<UserResponse>> register( UserRequest userRequest) throws MessagingException {


		if(userRepo.existsByUserEmail(userRequest.getUserEmail()))
		{
			throw new EmailAlreadyExist("User with given Email is Already Exist!!");
		}

		String otp=generateOTP();
		User user=mapToRespective(userRequest);
		usercachestore.add(userRequest.getUserEmail(), user);
		otpcachestore.add(userRequest.getUserEmail(), otp);
		
		try {
			sendOtpToMail(user, otp);
		} catch (MessagingException e) {
			
			log.error(" The Email Address Does Not Exist!! ");
		}

		if( userRepo.existsById(user.getUserId()))
		{
			confirmMail(user);
		}
		
		return ResponseEntityProxy.setResponseStructure(HttpStatus.ACCEPTED,
				"Please Verify through OTP send on Email Id"
				,mapToUserResponse(user));

	}


	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpmodel) {

		User user = usercachestore.get(otpmodel.getEmail());
		String otp = otpcachestore.get(otpmodel.getEmail());


		if(otp==null) throw new OtpExpiredException("OTP expired!!");
		if(user==null) throw new RegistrationSessionExpiredException("Registration Session Expired Please Try After 24 Hours");
		if(!otp.equals(otpmodel.getOtp())) throw new InvalidOTPException("Invalid OTP!!"); 

		user.setEmailVerified(true);
		userRepo.save(user);

		return ResponseEntityProxy.setResponseStructure(HttpStatus.CREATED,
				"Otp Verified Successfully And User Saved To Database Successfully!!"
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

	@Async
	private void sendMail(MessageStructure message) throws MessagingException
	{
		MimeMessage mimemessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mimemessage,true);

		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSendDate());
		helper.setText(message.getText(),true);

		javaMailSender.send(mimemessage);

	}

	private void sendOtpToMail(User user,String otp) throws MessagingException
	{
		sendMail( MessageStructure.builder()
				.to(user.getUserEmail())
				.subject("Complete Your Registration Process!!")
				.sendDate(new Date())
				.text(  "Hey !"+ user.getUserName() +"Good To See you Intrested in Electronics_Store"
						+"Complete your Registration using the OTP <br> "
						+"<h1> "+otp+"</h1> <br>"
						+"Note : the Otp expires in 1 minute"
						+"<br> <br>"
						+"with best Regards!!<br>"
						+"Electronics_Store!!"
						)
				.build());
	}
	
	private void confirmMail(User user) throws MessagingException
	{
		sendMail( MessageStructure.builder()
				.to(user.getUserEmail())
				.subject("Completed Your Registration Process Sucessfully!!")
				.sendDate(new Date())
				.text(  "Hey Namaste Guru ! "+ user.getUserName() +" Welcome to Electronics_Store Enjoy pandagooo!!"
						
						)
				.build());
	}

	
	private String generateOTP()
	{
		return String.valueOf(new Random().nextInt(100000,999999));
	}

}
