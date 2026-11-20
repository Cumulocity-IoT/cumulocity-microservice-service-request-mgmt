package cumulocity.microservice.service.request.mgmt.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.joda.JodaModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class MicroserviceConfig {

	/**
	 * Registers Joda-Time support with the auto-configured Jackson 3 JsonMapper
	 * used by Spring MVC for request and response bodies.
	 */
	@Bean
	public JodaModule jodaModule() {
		return new JodaModule();
	}

	@Bean
	public JsonMapperBuilderCustomizer dateTimeFormatCustomizer() {
		return builder -> builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setThreadNamePrefix("AsyncExecutor-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(60);
		executor.initialize();
		return executor;
	}
}
