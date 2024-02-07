package com.electronics_store.requestdto;

import com.electronics_store.enums.UserRole;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {
    
	
	private String userEmail;
	private String userPassword;

	private String userRole;

	
}
