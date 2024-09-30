package com.example.chating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatingApplication.class, args);
	}

}
