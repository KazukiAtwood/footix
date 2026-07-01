package com.footix.backend;

import com.footix.backend.config.WorldCupApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WorldCupApiProperties.class)
public class FootixBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootixBackendApplication.class, args);
	}

}
