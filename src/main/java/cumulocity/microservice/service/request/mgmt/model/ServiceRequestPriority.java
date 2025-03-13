package cumulocity.microservice.service.request.mgmt.model;

import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Schema(description = "Defines a custom specific priority.")
@RequiredArgsConstructor
@NoArgsConstructor
@Validated
public class ServiceRequestPriority {
	@Schema(required = true, description = "Priority name", example = "high")
	@NotNull
	@NonNull
	private String name;
	
	@Schema(required = true, description = "Priority ordinal", example = "1")
	@NotNull
	@NonNull
	private Long ordinal;
}
