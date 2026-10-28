package com.catholic.hangarae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HangaraeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HangaraeApplication.class, args);
	}

}
