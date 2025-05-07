package cumulocity.microservice.service.request.mgmt.model;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Defines a custom specific priority.")
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ServiceRequestPriority {
	@Schema(required = false, description = "Priority name", example = "high")
	private String name;
	
	@Schema(required = false, description = "Priority ordinal", example = "1")
	private Long ordinal;
}
