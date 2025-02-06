package cumulocity.microservice.service.request.mgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@EnableWebSecurity
@OpenAPIDefinition(
	    info = @Info(
	        title = "Cumulocity Service Request API",
	        version = "1.0"
		),
    	security = @SecurityRequirement(name = "basicAuth")
)

@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
@Configuration
public class AppSpecificSecurityConfig {

	@Bean
	public SecurityFilterChain apiFilterChanin(HttpSecurity http) throws Exception {
		http.antMatcher("/v3/api-docs");
		return http.build();
	}
}
