package com.cardinalsolutions.conference.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.cardinalsolutions.conference.repository")
@EntityScan("com.cardinalsolutions.conference.model")
public class ConferenceServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConferenceServicesApplication.class, args);
	}
}
