package com.wgf.consumer;

import com.wgf.base.annotation.EnableBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBase
@SpringBootApplication
public class ComsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComsumerApplication.class, args);
	}

}
