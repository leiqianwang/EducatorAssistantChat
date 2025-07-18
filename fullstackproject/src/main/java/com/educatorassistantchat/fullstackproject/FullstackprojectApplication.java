package com.educatorassistantchat.fullstackproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.educatorassistantchat.fullstackproject")
public class FullstackprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullstackprojectApplication.class, args);
	}

}
