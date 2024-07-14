package com.excitel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.excitel.external")
@CrossOrigin(origins = "*")
@EnableRetry
public class BroadbandPlanApplication {

	public static void main(String[] args) {
		SpringApplication.run(BroadbandPlanApplication.class, args);
	}

}
