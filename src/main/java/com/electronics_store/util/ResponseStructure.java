package com.electronics_store.util;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseStructure<T>{
	
	private int status;
	private String message;
	private T data;

}
