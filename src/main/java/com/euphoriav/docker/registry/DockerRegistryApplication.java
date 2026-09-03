package com.euphoriav.docker.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DockerRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DockerRegistryApplication.class, args);
	}
}
