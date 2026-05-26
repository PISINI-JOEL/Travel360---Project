package com.cts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfiguration {

	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .info(new Info()
	            .title("Travel360")
	            .version("1.0")
	            .description("It allows to create web api end points to manage travel related bookings")
	            .contact(new Contact().name("Travel360-front office").email("travel360@gmail.com")));
	}
}
