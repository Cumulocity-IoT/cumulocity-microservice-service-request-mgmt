package cumulocity.microservice.service.request.mgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@OpenAPIDefinition(
	    info = @Info(
	        title = "Cumulocity Service Request API",
	        version = "1.0"
	    )
	)
@Configuration
public class AppSpecificSecurityConfig {

	@Bean
	public SecurityFilterChain apiFilterChanin(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeRequests ->
			authorizeRequests
				.requestMatchers("/v3/api-docs").permitAll() // Exclude this path from security
				.anyRequest().authenticated()
		)
		.formLogin(withDefaults());

		return http.build();
	}
}
