package com.jhw.potd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PotdApplication {

	public static void main(String[] args) {
		SpringApplication.run(PotdApplication.class, args);
	}

}
