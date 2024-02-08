package com.electronics_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ElectronicsShoppingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicsShoppingApplication.class, args);
		System.out.println("Running-Electronics-Shopping-Application!!");
	}

}
