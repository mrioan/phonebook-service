package com.centralway.assessment.phonebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.centralway.assessment.phonebook.model")
@EnableJpaRepositories("com.centralway.assessment.phonebook.repositories")
@PropertySource("classpath:db-config.properties")
public class PhonebookWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhonebookWebApplication.class, args);
	}

}
