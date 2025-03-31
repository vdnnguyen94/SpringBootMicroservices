package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class VanNguyenSeyeonJoManagerClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(VanNguyenSeyeonJoManagerClientApplication.class, args);
		System.out.println("Manger Client App is running on: http://localhost:3003");
		System.out.println("REST API is running on: http://localhost:3002");
		System.out.println("Eureka Server: http://localhost:3001");
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
