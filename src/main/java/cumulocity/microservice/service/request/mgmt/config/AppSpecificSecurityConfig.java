package cumulocity.microservice.service.request.mgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class AppSpecificSecurityConfig {

	@Bean
	public SecurityFilterChain apiFilterChanin(HttpSecurity http) throws Exception {
		http.antMatcher("/v3/api-docs");
		return http.build();
	}
}
