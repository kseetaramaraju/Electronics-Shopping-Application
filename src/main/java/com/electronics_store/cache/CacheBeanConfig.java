package com.electronics_store.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.electronics_store.entity.User;

@Configuration
public class CacheBeanConfig {

	@Bean
	public CacheStore<User> userCacheStore()
	{
		return new CacheStore<User>(Duration.ofMinutes(5));
	}
	
	@Bean
	public CacheStore<String> otpCacheStore()
	{
		return new CacheStore<String>(Duration.ofSeconds(20));
	}
	
	
}