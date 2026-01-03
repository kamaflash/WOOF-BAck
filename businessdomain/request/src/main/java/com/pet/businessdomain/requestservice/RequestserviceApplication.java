package com.pet.businessdomain.requestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class RequestserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestserviceApplication.class, args);
	}
	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalanceWebClientBuilder() {
		return WebClient.builder();
	}

}
