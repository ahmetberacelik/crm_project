package com.crm.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the User Service application.
 * This class initializes and runs the Spring Boot application.
 */
@SpringBootApplication
public class UserServiceApplication {

	/**
	 * The main method which serves as the entry point for the Java Virtual Machine (JVM).
	 *
	 * @param args command line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

} 