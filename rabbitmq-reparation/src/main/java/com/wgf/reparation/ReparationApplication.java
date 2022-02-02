package com.wgf.reparation;

import com.wgf.base.annotation.EnableBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBase
@EnableScheduling
@EnableFeignClients(basePackages = "com.wgf.reparation.api")
@SpringBootApplication
public class ReparationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReparationApplication.class, args);
	}

}
