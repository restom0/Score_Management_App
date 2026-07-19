package com.vn.golden_owl_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI goldenOwlOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("G-Scores API")
						.version("v1")
						.description("REST API for Vietnam National Exam 2024 score lookup, subject statistics, and Group A ranking.")
						.contact(new Contact()
								.name("Golden Owl Webdev Intern Assignment 3")))
				.addServersItem(new Server()
						.url("http://localhost:8080")
						.description("Local backend"));
	}
}
