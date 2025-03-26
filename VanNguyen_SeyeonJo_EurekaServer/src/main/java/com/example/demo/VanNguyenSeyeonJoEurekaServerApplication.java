package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class VanNguyenSeyeonJoEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VanNguyenSeyeonJoEurekaServerApplication.class, args);
		System.out.println("Eureka Server started on PORT 3001");
	}

}
